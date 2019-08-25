import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Pattern;

/*
 * 从JVM启动文件当中抓取jar包名称
 * 输入参数:输入路径,输出路径(自创建)
 *
 * */
public class FindAndPrintJars {
    //args[0] input path    args[1] output path
    public static void main(String[] args) throws IOException {
        TreeSet<String> strings = readFromInputFile(args[0]);
        writeJarName2File(strings, args[1]);
    }

    /*
    * 根据输入的文件路径获取jar包名称,添加到TreeSet集合当中
    * */
    private static TreeSet<String> readFromInputFile(String inputPath) throws IOException {
        TreeSet<String> strings = new TreeSet<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            Optional<String> jarName = getJarName(line);
            if (jarName.isPresent())
                strings.add(jarName.get());
        }
        return strings;

    }

    /*
    * 根据集合中的jar包名称,和输出的路径,将jarName输出
    * 使用apache的IoUtil包..
    * */
    private static void writeJarName2File(TreeSet<String> strings, String outputPath) throws IOException {
        File file = initOutputPath(outputPath);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for (String string : strings) {
            FileUtils.writeByteArrayToFile(file, string.getBytes(), true);
            FileUtils.writeByteArrayToFile(file, "\r\n".getBytes(), true);
            bufferedWriter.write(string);
            bufferedWriter.write("\r\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    /*
    * 手动创建输出字符流的方式,输出jar包名称.
    * */
    private static void writeJarName2File2(TreeSet<String> strings, String outputPath) throws IOException {
        File file = initOutputPath(outputPath);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        for (String string : strings) {
            bufferedWriter.write(string);
            bufferedWriter.write("\r\n");

            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    /*
     * 输出路径初始化工作-如果不存在创建父路径,创建文件.
     * */
    private static File initOutputPath(String outputPath) throws IOException {
        File file = new File(outputPath);
        if (!file.exists()) {
            String parent = file.getParent();
            new File(parent).mkdirs();
            file.createNewFile();
        }
        return file;
    }

    private static Optional<String> getJarName(String line) {
        char[] chars = line.toCharArray();
        if (chars.length == 0 || chars[0] == '<') return Optional.empty();

        //Pattern pattern = Pattern.compile("/");

        String[] split = line.split("/");
        if (split[split.length - 1] == null) return Optional.empty();
        return Optional.ofNullable(split[split.length - 1]);
    }

}
