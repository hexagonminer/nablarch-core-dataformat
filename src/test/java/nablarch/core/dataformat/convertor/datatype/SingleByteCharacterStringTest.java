package nablarch.core.dataformat.convertor.datatype;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.DataRecordFormatter;
import nablarch.core.dataformat.FieldDefinition;
import nablarch.core.dataformat.FormatterFactory;
import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.dataformat.SyntaxErrorException;
import nablarch.test.support.tool.Hereis;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.nio.charset.Charset;

import static nablarch.core.dataformat.DataFormatTestUtils.createInputStreamFrom;
import static nablarch.test.StringMatcher.endsWith;
import static nablarch.test.StringMatcher.startsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * シングルバイト文字コンバータのテスト。
 * 
 * 観点：
 * 正常系はフォーマッタのテストで確認しているので、ここでは異常系のテストを行う。
 *   ・レイアウト定義ファイルのパラメータ不正
 *   ・レイアウト定義ファイルで設定したフィールド長を超えるサイズの書き込み
 *   ・パディング文字に２バイト文字を設定
 * 
 * @author Masato Inoue
 */
public class SingleByteCharacterStringTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * シングルバイトのパラメータが数値型でない場合に、例外がスローされることの確認。
     */
    @Test
    public void testInvalidLayout1() throws Exception {
        
        File formatFile = Hereis.file("./format.fmt");
        /**********************************************
        # ファイルタイプ
        file-type:    "Fixed"
        # 文字列型フィールドの文字エンコーディング
        text-encoding: "sjis"
        # 各レコードの長さ
        record-length: 10

        # データレコード定義
        [Default]
        1    singleByteString     X("a")   # 文字列（不正な値）
        ***************************************************/
        formatFile.deleteOnExit();
        
        InputStream source = createInputStreamFrom("0123456789");

        DataRecordFormatter formatter = 
            FormatterFactory.getInstance().setCacheLayoutFileDefinition(false).createFormatter(new File("format.fmt"));
        
        try {
            formatter.setInputStream(source).initialize();
            fail();
        } catch (SyntaxErrorException e) {
            assertThat(e.getMessage(), startsWith(
                    "invalid parameter type was specified. 1st parameter must be an integer. " +
                            "parameter=[a]. convertor=[SingleByteCharacterString]."));
            assertThat(e.getFilePath(), endsWith("format.fmt"));       
        }
    }
    
    
    /**
     * レイアウト定義ファイルで設定したフィールドの長さを超える文字列を書きこもうとした場合に、例外がスローされることの確認。
     */
    @Test
    public void testInvalidLayout3() throws Exception {
        
        File formatFile = Hereis.file("./format.fmt");
        /**********************************************
        # ファイルタイプ
        file-type:    "Fixed"
        # 文字列型フィールドの文字エンコーディング
        text-encoding: "sjis"
        # 各レコードの長さ
        record-length: 10

        # データレコード定義
        [Default]
        1    singleByteString     X(10)   # 文字列（不正な値）
        ***************************************************/
        formatFile.deleteOnExit();

        FileOutputStream outputStream = new FileOutputStream("test.dat");

        DataRecordFormatter formatter = 
            FormatterFactory.getInstance().setCacheLayoutFileDefinition(false).createFormatter(formatFile);
        formatter.setOutputStream(outputStream).initialize();
        
        DataRecord dataRecord = new DataRecord(){{
            put("singleByteString", "01234567890"); // 11バイトの文字を書き込む
        }};

        try {
            formatter.writeRecord(dataRecord);
            fail();
        } catch (InvalidDataFormatException e) {
        	assertTrue(e.getMessage().contains("too large data."));
        	assertTrue(e.getMessage().contains("field size = '10' data size = '11"));
        	assertTrue(e.getMessage().contains("data: 01234567890"));
        	assertTrue(e.getMessage().contains("field name=[singleByteString]"));
        }
        formatter.close();
    }
    
    /**
     * パディング文字に２バイト文字を設定した場合、例外がスローされることの確認。
     */
    @Test
    public void testInvalidLayout4() throws Exception {
        
        File formatFile = Hereis.file("./format.fmt");
        /**********************************************
        # ファイルタイプ
        file-type:    "Fixed"
        # 文字列型フィールドの文字エンコーディング
        text-encoding: "sjis"
        # 各レコードの長さ
        record-length: 10

        # データレコード定義
        [Default]
        1    singleByteString     X(10) pad("　")   # ２バイトの全角空白を設定
        ***************************************************/
        formatFile.deleteOnExit();

        FileOutputStream outputStream = new FileOutputStream("test.dat");

        DataRecordFormatter formatter = 
            FormatterFactory.getInstance().setCacheLayoutFileDefinition(false).createFormatter(formatFile);
        formatter.setOutputStream(outputStream).initialize();
        
        DataRecord dataRecord = new DataRecord(){{
            put("singleByteString", "012345678"); // 11バイトの文字を書き込む
        }};

        try {
            formatter.writeRecord(dataRecord);
            fail();
        } catch (SyntaxErrorException e) {
            assertEquals(
                    "invalid parameter was specified. the length of padding string must be 1. but specified one was 2 byte long.",
                    e.getMessage());
        }
        formatter.close();
    }
    

    /**
     * シングルバイトのパラメータが存在しない場合に、例外がスローされることの確認。
     */
    @Test
    public void testInvalidLayout5() throws Exception {
        
        File formatFile = Hereis.file("./format.fmt");
        /**********************************************
        # ファイルタイプ
        file-type:    "Fixed"
        # 文字列型フィールドの文字エンコーディング
        text-encoding: "sjis"
        # 各レコードの長さ
        record-length: 10

        # データレコード定義
        [Default]
        1    singleByteString     X()   # 文字列（不正な値）
        ***************************************************/
        formatFile.deleteOnExit();
        
        InputStream source = createInputStreamFrom("0123456789");

        DataRecordFormatter formatter = 
            FormatterFactory.getInstance().setCacheLayoutFileDefinition(false).createFormatter(new File("format.fmt"));
        
        try {
            formatter.setInputStream(source).initialize();
            fail();
        } catch (SyntaxErrorException e) {
            assertThat(e.getMessage(), startsWith(
                    "parameter was not specified. parameter must be specified. " +
                            "convertor=[SingleByteCharacterString]."));
            assertThat(e.getFilePath(), endsWith("format.fmt"));
        }
    }

    /**
     * 初期化時にnullをわたすと例外がスローされること。
     */
    @Test
    public void testInitializeNull() {
        SingleByteCharacterString datatype = new SingleByteCharacterString();

        exception.expect(SyntaxErrorException.class);
        exception.expectMessage("initialize parameter was null. parameter must be specified. convertor=[SingleByteCharacterString].");

        datatype.initialize(null);
    }

    /**
     * 初期化時のパラメータ不正テスト。
     */
    @Test
    public void initializeArgError(){
        
        /**
         * 引数がnull。
         */
        
        SingleByteCharacterString zonedDecimal = new SingleByteCharacterString();
        try {
            zonedDecimal.initialize(null, "hoge");
            fail();
        } catch (SyntaxErrorException e) {
            assertEquals("1st parameter was null. parameter=[null, hoge]. convertor=[SingleByteCharacterString].", e.getMessage());
        }
        
    }

    /**
     * 入力時にパラメータが空白の場合のテスト。
     */
    @Test
    public void testReadEmpty() throws Exception {
        SingleByteCharacterString singleByteCharacter = new SingleByteCharacterString();
        singleByteCharacter.init(new FieldDefinition().setEncoding(Charset.forName("MS932")), 10);
        assertThat("", is(singleByteCharacter.convertOnRead("".getBytes())));
    }

    /**
     * 出力時にパラメータがnullまたは空白の場合のテスト。
     */
    @Test
    public void testWriteParameterNullOrEmpty() throws Exception {
        SingleByteCharacterString singleByteCharacter = new SingleByteCharacterString();
        singleByteCharacter.init(new FieldDefinition().setEncoding(Charset.forName("MS932")), 10);
        assertThat("          ".getBytes("MS932"), is(singleByteCharacter.convertOnWrite(null)));
        assertThat("          ".getBytes("MS932"), is(singleByteCharacter.convertOnWrite("")));
    }

    /**
     * 出力時にパラメータがnullの場合にデフォルト値を出力するテスト。
     */
    @Test
    public void testWriteDefault() throws Exception {

        File formatFile = Hereis.file("./format.fmt");
        /**********************************************
         # ファイルタイプ
         file-type:    "Fixed"
         # 文字列型フィールドの文字エンコーディング
         text-encoding: "sjis"
         # 各レコードの長さ
         record-length: 10

         # データレコード定義
         [Default]
         1    singleByteString     X(10)   "abc"
         ***************************************************/
        formatFile.deleteOnExit();

        File outputFile = new File("test.dat");
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        DataRecordFormatter formatter =
                FormatterFactory.getInstance().setCacheLayoutFileDefinition(false).createFormatter(formatFile);
        formatter.setOutputStream(outputStream).initialize();

        DataRecord dataRecord = new DataRecord(){{
            put("singleByteString", null);
        }};

        formatter.writeRecord(dataRecord);
        formatter.close();

        assertThat(readLineFrom(outputFile, "sjis"), is("abc       "));
    }
    /** 指定ファイルから一行読み込む */
    private String readLineFrom(File outputFile, String encoding)
            throws UnsupportedEncodingException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(outputFile), encoding));
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
