package smiley.javasweeper.filestorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import smiley.javasweeper.model.Board;
import smiley.javasweeper.model.squares.BombSquare;
import smiley.javasweeper.model.squares.NumberSquare;
import smiley.javasweeper.model.squares.Square;

public class BoardLoader {
    private static final byte MAJOR_VERSION = 1; // Max 32
    private static final byte MINOR_VERSION = 0; // Max 32
    private static final byte PATCH_VERSION = 0; // Max 64
    private static final List<Class<? extends Square>> SQUARE_IDS = List.of(
            NumberSquare.class,
            BombSquare.class
    );

    private BoardLoader() {
        throw new IllegalStateException("Utility class");
    }

    private static Square makeNumberSquare(String squareString) {
        String[] squareArr = squareString.split("&");
        Square square;
        int x = Integer.parseInt(squareArr[1]);
        int y = Integer.parseInt(squareArr[2]);
        if (Objects.equals(squareArr[0], "number")) {
            square = new NumberSquare(x, y);
            NumberSquare numSquare = (NumberSquare) square;
            if (Boolean.parseBoolean(squareArr[4])) {
                numSquare.setRevealedTrue(Integer.parseInt(squareArr[5]));
            }
        } else if (Objects.equals(squareArr[0], "bomb")) {
            square = new BombSquare(x, y);
        } else {
            throw new IllegalArgumentException("Cannot recognize square: " + squareArr[0]);
        }
        if (Boolean.parseBoolean(squareArr[3])) {
            square.toggleFlagged();
        }
        return square;
    }

    private static Square makeSquare(ByteMap byteMap) {
        Class<? extends Square> squareClass = SQUARE_IDS.get(byteMap.getByte(Template.Keys.TYPE, (byte) 1));
        int x = byteMap.getInt(Template.Keys.X, 0);
        int y = byteMap.getInt(Template.Keys.Y, 0);
        byte flags = byteMap.getByte(Template.Keys.FLAGS, (byte) 0);
        boolean flagged = (flags & 1) == 1;
        boolean revealed = ((flags >>> 1) & 1) == 1;
        Square square;
        if (squareClass == NumberSquare.class) {
            NumberSquare numberSquare = new NumberSquare(x, y);
            square = numberSquare;
            if (revealed) {
                numberSquare.setRevealedTrue(byteMap.getByte(Template.Keys.NUMBER, (byte) 0));
            }
        } else {
            square = new BombSquare(x, y);
            if (revealed) {
                square.setRevealedTrue();
            }
        }
        if (flagged) {
            square.toggleFlagged();
        }
        return square;
    }

    public static Board makeBoard(ByteMap byteMap, String filename) {
        Board board = new Board(byteMap.getDouble(
                Template.Keys.MINE_CHANCE,
                Settings.getDefault(Settings.Keys.MINE_CHANCE, Double.class)
        ), filename);

        for (ByteMap squareMap : byteMap.getSubMaps(Template.Keys.SQUARES)) {
            Square square = makeSquare(squareMap);
            board.put(square.getX(), square.getY(), square);
        }
        return board;
    }

    public static Board loadBoard(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            short versionBytes = ByteBuffer.wrap(fis.readNBytes(2)).getShort();
            byte majorVersion = (byte) ((versionBytes >>> 11) & 0b11111);
            byte minorVersion = (byte) ((versionBytes >>> 6) & 0b11111);
            byte patchVersion = (byte) (versionBytes & 0b111111);
            if (majorVersion != MAJOR_VERSION
                    || minorVersion != MINOR_VERSION
                    || patchVersion != PATCH_VERSION
            ) {
                throw new IOException("Save file is from a different game version");
            }
            return makeBoard(Template.TOP_LEVEL.getByteMap(fis), filename);
        }
    }

    public static boolean boardExists(String filename) {
        File f = new File(filename);
        return f.exists() && !f.isDirectory();
    }

    private static void serializeSquare(Serializer serializer, Square square) {
        for (String key : Template.SQUARE.getTemplate().keySet()) {
            switch (key) {
                case (Template.Keys.TYPE) -> serializer.write((byte) SQUARE_IDS.indexOf(square.getClass()));
                case (Template.Keys.X) -> serializer.write(ByteBuffer.allocate(4).putInt(square.getX()).array());
                case (Template.Keys.Y) -> serializer.write(ByteBuffer.allocate(4).putInt(square.getY()).array());
                case (Template.Keys.FLAGS) -> {
                    byte flags = (byte) ((square.isFlagged() ? 0b1 : 0)
                            | (square.isRevealed() ? 0b10 : 0));
                    serializer.write(flags);
                }
                case ("number") -> {
                    if (square.isRevealed() && square instanceof NumberSquare numberSquare) {
                        serializer.writeDynamicByteLength(new byte[]{(byte) numberSquare.getNumber()});
                    } else {
                        serializer.writeDynamicByteLength(new byte[0]);
                    }
                }
            }
        }
    }

    private static void serializeBoard(Serializer serializer, Board board) {
        for (String key : Template.BOARD.getTemplate().keySet()) {
            switch (key) {
                case (Template.Keys.MINE_CHANCE) -> serializer.write(ByteBuffer.allocate(8).putDouble(board.getMineChance()).array());
                case (Template.Keys.SQUARES) -> {
                    for (Square square : board) {
                        serializeSquare(serializer, square);
                    }
                }
            }
        }
    }

    private static Serializer serialize(Board board) {
        Serializer serializer = new Serializer();
        short versionBytes = ((MAJOR_VERSION & 0b11111) << 11)
                | ((MINOR_VERSION & 0b11111) << 6)
                | (PATCH_VERSION & 0b111111);
        serializer.write(ByteBuffer.allocate(2).putShort(versionBytes).array());

        serializeBoard(serializer, board);
        return serializer;
    }

    public static void saveBoard(Board board) throws IOException {
        String filename = board.getFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Board \"board\" has no filename");
        }
         serialize(board).writeToFile(filename);
    }

    private static class Serializer {
        private final ByteArrayOutputStream byteStream;

        public Serializer() {
            this.byteStream = new ByteArrayOutputStream();
        }

        public void write(byte b) {
            byteStream.write(b);
        }

        public void write(byte[] b) {
            byteStream.write(b, 0, b.length);
        }

        public void writeDynamicByteLength(byte[] b) {
            if (b.length > 255) {
                throw new IllegalArgumentException("Dynamic length array cannot be longer than 255 bytes");
            }
            write((byte) b.length);
            write(b);
        }

        public void writeToFile(String filename) throws IOException {
            File file = new File(filename);
            if (file.createNewFile()) {
                Logger.getLogger(getClass().getName()).log(Level.INFO, "Created file \"{0}\"", filename);
            }
            try (FileOutputStream fos = new FileOutputStream(file)) {
                byteStream.writeTo(fos);
            }
        }
    }

    private enum Template {
        BOARD(Stream.of(
                new AbstractMap.SimpleEntry<>(Keys.MINE_CHANCE, 8),
                new AbstractMap.SimpleEntry<>(Keys.SQUARES, -2)
        )),
        SQUARE(Stream.of(
                new AbstractMap.SimpleEntry<>(Keys.TYPE, 1),
                new AbstractMap.SimpleEntry<>(Keys.X, 4),
                new AbstractMap.SimpleEntry<>(Keys.Y, 4),
                new AbstractMap.SimpleEntry<>(Keys.FLAGS, 1),
                new AbstractMap.SimpleEntry<>(Keys.NUMBER, -1)
        ));

        public static final Template TOP_LEVEL = BOARD;

        /**
         * Read the next byte, then read as many more bytes forward as the value of the first byte read.
         */
        private static final int DYNAMIC_LENGTH = -1;
        /**
         * Execute square template infinitely, until end of stream.
         */
        private static final int EXECUTE_SQUARE_INFINITELY = -2;

        private final Map<String, Integer> template;

        Template(Stream<Map.Entry<String, Integer>> template) {
            this.template = template.collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (v1, v2) -> v2,
                    LinkedHashMap::new
            ));
        }

        public Map<String, Integer> getTemplate() {
            return template;
        }

        public ByteMap getByteMap(InputStream is) throws IOException {
            ByteMap byteMap = new ByteMap();
            for (Map.Entry<String, Integer> entry : template.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                switch (value) {
                    case DYNAMIC_LENGTH -> byteMap.put(key, is.readNBytes(is.read()));
                    case EXECUTE_SQUARE_INFINITELY -> {
                        while (is.available() != 0) {
                            byteMap.addSubMap(key, SQUARE.getByteMap(is));
                        }
                    }
                    default -> {
                        if (value < 0) {
                            throw new IllegalStateException("Template has invalid special operation: " + value);
                        }
                        byteMap.put(key, is.readNBytes(value));
                    }
                }
            }
            return byteMap;
        }

        public static class Keys {
            public static final String MINE_CHANCE = "mineChance";
            public static final String SQUARES = "squares";
            public static final String TYPE = "type";
            public static final String X = "x";
            public static final String Y = "y";
            public static final String FLAGS = "flags";
            public static final String NUMBER = "number";

            private Keys() {
                throw new IllegalStateException("Utility class");
            }
        }
    }

    private static class ByteMap extends HashMap<String, byte[]> {
        private final Map<String, Collection<ByteMap>> subMaps;

        public ByteMap() {
            super();
            this.subMaps = new HashMap<>();
        }

        private byte[] getEnsuredLength(String key, int length) {
            byte[] value = get(key);
            if (value == null || value.length != length) {
                throw new UnsupportedOperationException("Value does not have expected length");
            }
            return value;
        }

        public byte getByte(String key, byte fallback) {
            byte value;
            try {
                value = getEnsuredLength(key, 1)[0];
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public short getShort(String key, short fallback) {
            short value;
            try {
                value =  ByteBuffer.wrap(getEnsuredLength(key, 2)).getShort();
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public int getInt(String key, int fallback) {
            int value;
            try {
                value = ByteBuffer.wrap(getEnsuredLength(key, 4)).getInt();
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public long getLong(String key, long fallback) {
            long value;
            try {
                value = ByteBuffer.wrap(getEnsuredLength(key, 8)).getLong();
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public float getFloat(String key, float fallback) {
            float value;
            try {
                value = ByteBuffer.wrap(getEnsuredLength(key, 4)).getFloat();
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public double getDouble(String key, double fallback) {
            double value;
            try {
                value = ByteBuffer.wrap(getEnsuredLength(key, 8)).getDouble();
            } catch (UnsupportedOperationException uoe) {
                value = fallback;
            }
            return value;
        }

        public String getString(String key) {
            return new String(get(key), StandardCharsets.UTF_8);
        }

        public void addSubMap(String key, ByteMap subMap) {
            subMaps.computeIfAbsent(key, k -> new HashSet<>());
            subMaps.get(key).add(subMap);
        }

        public Collection<ByteMap> getSubMaps(String key) {
            return subMaps.get(key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            ByteMap byteMap = (ByteMap) o;
            return subMaps.equals(byteMap.subMaps);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), subMaps);
        }
    }
}
