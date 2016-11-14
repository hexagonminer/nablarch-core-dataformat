package nablarch.core.dataformat.convertor.datatype;

import nablarch.core.dataformat.FieldDefinition;
import nablarch.core.dataformat.InvalidDataFormatException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link SignedPackedDecimal}のテスト。
 *
 * @author TIS
 */
public class SignedPackedDecimalTest {

    private SignedPackedDecimal sut = new SignedPackedDecimal();
    private FieldDefinition field = new FieldDefinition().setEncoding(Charset.forName("sjis"));
    final byte packNibble = 0x03;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private void setParameter(byte packNibble, int signNibblePositive, int signNibbleNegative) {
        sut.setPackNibble(packNibble);
        sut.setPackSignNibblePositive(signNibblePositive);
        sut.setPackSignNibbleNegative(signNibbleNegative);
    }

    /**
     * ASCII規格での符号ありパック10進の正常系読込テスト。
     * 正の数。
     */
    @Test
    public void testReadNormalPositiveNumber() throws Exception {
        sut.init(field, 5, 0);
        setParameter((byte) 0x03, 3, 7);

        byte[] inputBytes = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x13
        };

        assertThat(sut.convertOnRead(inputBytes), is(new BigDecimal("87654321")));
    }

    /**
     * ASCII規格での符号ありパック10進の正常系書き込みテスト。
     * 正の数。
     */
    @Test
    public void testWriteNormalPositiveNumber() throws Exception {
        sut.init(field, 5, 0);
        setParameter((byte) 0x03, 3, 7);

        byte[] expected = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x13
        };

        assertThat(sut.convertOnWrite(new BigDecimal("87654321")), is(expected));
    }

    /**
     * ASCII規格での符号ありパック10進の正常系読込テスト。
     * 負の数。
     */
    @Test
    public void testReadNormalNegativeNumber() throws Exception {
        sut.init(field, 5, 0);
        setParameter((byte) 0x03, 3, 7);

        byte[] inputBytes = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x17
        };

        assertThat(sut.convertOnRead(inputBytes), is(new BigDecimal("-87654321")));
    }

    /**
     * ASCII規格での符号ありパック10進の正常系書き込みテスト。
     * 負の数。
     */
    @Test
    public void testWriteNormalNegativeNumber() throws Exception {
        sut.init(field, 5, 0);
        setParameter((byte) 0x03, 3, 7);

        byte[] expected = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x17
        };

        assertThat(sut.convertOnWrite("-87654321"), is(expected));
    }

    /**
     * ASCII規格での符号ありパック10進の異常系読込テスト。
     * 符号ビットが不正。
     */
    @Test
    public void testReadAbnormal() throws Exception {
        sut.init(field, 5, 0);
        setParameter((byte) 0x03, 3, 7);

        byte[] inputBytes = new byte[] {
                0x08, 0x76, 0x54, 0x32, 0x16
        };

        exception.expect(InvalidDataFormatException.class);
        exception.expectMessage("invalid pack bits was specified.");

        sut.convertOnRead(inputBytes);
    }

    /**
     * EBCDIC規格での符号ありパック10進の正常系読込テスト。
     * 正の数。
     */
    @Test
    public void testReadNormalPositiveNumberEBCDIC() throws Exception {
        final FieldDefinition field  = new FieldDefinition();
        field.setEncoding(Charset.forName("IBM1047"));
        sut.init(field, 5, 0);
        setParameter((byte) 0xF0, Integer.parseInt("C", 16), Integer.parseInt("D", 16));

        byte[] inputBytes = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x1C
        };

        assertThat(sut.convertOnRead(inputBytes), is(new BigDecimal("87654321")));
    }

    /**
     * EBCDIC規格での符号ありパック10進の正常系書き込みテスト。
     * 正の数。
     */
    @Test
    public void testWriteNormalPositiveNumberEBCDIC() throws Exception {
        final FieldDefinition field  = new FieldDefinition();
        field.setEncoding(Charset.forName("IBM1047"));
        sut.init(field, 5, 0);
        setParameter((byte) 0xF0, Integer.parseInt("C", 16), Integer.parseInt("D", 16));

        byte[] expected = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x1C
        };

        assertThat(sut.convertOnWrite("87654321"), is(expected));
    }

    /**
     * EBCDIC規格での符号ありパック10進の正常系読込テスト。
     * 負の数。
     */
    @Test
    public void testReadNormalNegativeNumberEBCDIC() throws Exception {
        final FieldDefinition field  = new FieldDefinition();
        field.setEncoding(Charset.forName("IBM1047"));
        sut.init(field, 5, 0);
        setParameter((byte) 0xF0, Integer.parseInt("C", 16), Integer.parseInt("D", 16));

        byte[] inputBytes = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x1D
        };

        assertThat(sut.convertOnRead(inputBytes), is(new BigDecimal("-87654321")));
    }

    /**
     * EBCDIC規格での符号ありパック10進の正常系書き込みテスト。
     * 負の数。
     */
    @Test
    public void testWriteNormalNegativeNumberEBCDIC() throws Exception {
        final FieldDefinition field  = new FieldDefinition();
        field.setEncoding(Charset.forName("IBM1047"));
        sut.init(field, 5, 0);
        setParameter((byte) 0xF0, Integer.parseInt("C", 16), Integer.parseInt("D", 16));

        byte[] expected = new byte[]{
                0x08, 0x76, 0x54, 0x32, 0x1D
        };

        assertThat(sut.convertOnWrite("-87654321"), is(expected));
    }

    /**
     * 最大値の書き込みテスト。
     * 正の整数。
     */
    @Test
    public void testWriteMaxValue() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        byte[] maxBytes = new byte[] {
                0x09, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x94
        };

        Assert.assertThat(sut.convertOnWrite("999999999999999999"), is(maxBytes));
    }

    /**
     * 最大値+1の書き込みテスト。
     * 正の整数。
     */
    @Test
    public void testWriteMaxValuePlus1() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        exception.expect(InvalidDataFormatException.class);
        exception.expectMessage("invalid parameter was specified. the number of parameter digits must be 18 or less, but was '19'. parameter=[1000000000000000000].");

        sut.convertOnWrite("1000000000000000000");
    }

    /**
     * 最小値の書き込みテスト。
     * 負の整数。
     */
    @Test
    public void testWriteMinValue() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        byte[] maxBytes = new byte[] {
                0x09, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x97
        };

        Assert.assertThat(sut.convertOnWrite("-999999999999999999"), is(maxBytes));
    }

    /**
     * 負の小数の最大桁数の書き込みテスト。
     */
    @Test
    public void testWriteMinusValueMaxLength() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        byte[] maxBytes = new byte[] {
                0x09, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x99, (byte)0x97
        };

        Assert.assertThat(sut.convertOnWrite("-99999999999.9999999"), is(maxBytes));
    }

    /**
     * 負数の最大桁数+1の書き込みテスト。
     * 負の整数。
     */
    @Test
    public void testWriteMinValueMinus1() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        exception.expect(InvalidDataFormatException.class);
        exception.expectMessage("invalid parameter was specified. the number of parameter digits must be 18 or less, but was '19'. parameter=[-1000000000000000000].");

        sut.convertOnWrite("-1000000000000000000");
    }

    /**
     * 負数の最大桁数+1の書き込みテスト。
     * 負の小数。
     */
    @Test
    public void testWriteMinValueMaxLengthPlus1() throws Exception {
        sut.init(field, 10, 0);
        setParameter((byte) 0x30, 4, 7);

        exception.expect(InvalidDataFormatException.class);
        exception.expectMessage("invalid parameter was specified. the number of unscaled parameter digits must be 18 or less, but was '19'. unscaled parameter=[-1000000000000000000], original parameter=[-10000000000000.00000].");

        sut.convertOnWrite("-10000000000000.00000");
    }
}
