/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeSecurityTestUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeSecurityTestUtil.class);
    public static final String FILE_ABC_TXT = "file:abc.txt";
    public static final String IPSEC_DATA_FILE_PATH = "/data/IPsec/";

    /**
     * Convert stream to file
     *
     * @param inputStream
     *            input stream of file
     * @param fileName
     *            name of the file
     * @return file
     */
    public static File inputStreamToFile(final InputStream inputStream,
            final String fileName) {
        Reader reader;
        Writer writer;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            reader = fileName.endsWith("xml") ? new InputStreamReader(
                    inputStream) : new InputStreamReader(inputStream, "UTF-8");

            writer = fileName.endsWith("xml") ? new OutputStreamWriter(
                    new FileOutputStream(fileName))
                    : new OutputStreamWriter(
                            new FileOutputStream(fileName), "UTF-8");

            bufferedWriter = new BufferedWriter(writer);
            bufferedReader = new BufferedReader(reader);
            String message;
            while ((message = bufferedReader.readLine()) != null) {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new File(fileName);
    }

    /**
     * Get stream from file on a relative path
     *
     * @param absoluteFilePath
     *            - relative file path
     * @return stream
     */
    public static InputStream getStreamFromAbsoluteFilePath(
            final String absoluteFilePath) {
        try {
            final InputStream inputStream = NodeSecurityTestUtil.class.getResource(
                    absoluteFilePath).openStream();
            return inputStream;
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * display content of a text file
     *
     * @param file
     *            - file handler to read file
     */
    public static void displayTextFile(final File file) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("File Name : " + file.getName());
                LOGGER.debug("File content : "
                        + FileUtils.readFileToString(file));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create byte array for string
     *
     * @param data
     *            string data for converting to bytes
     */
    public static byte[] createBytesFromString(final String data) {
        byte[] byteData = null;
        try {
            byteData = data.trim().getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return byteData;
    }

    /**
     * create file from byte array
     *
     * @param data
     *            byte data for writing to file
     * @return File
     */
    public static File createFileFromByte(final byte[] data, String fileName) {
        fileName = StringUtils.isBlank(fileName) ? FILE_ABC_TXT : fileName;
        final File file = new File(fileName);
        try {
            FileUtils.writeByteArrayToFile(file, data);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * load template file to a hashmap
     *
     * @param fileName
     *            file name for loading
     * @param relPath
     *            relative path of the file
     * @return Map
     */
    public static Map<String, Object> templateLoader(final String fileName, String relPath) {
        final Map<String, Object> properties = new HashMap<>();
        relPath = StringUtils.isBlank(relPath) ? IPSEC_DATA_FILE_PATH : relPath;
        properties.put("fileName", fileName);
        properties.put("filePath", relPath + fileName);
        return Collections.unmodifiableMap(properties);
    }

    /**
     * split string to token using reg exp
     *
     * @param string
     *            string for matching
     * @param regExp
     *            regular expression for pattern matching
     * @return list of token
     */
    @SuppressWarnings("unchecked")
    public static List<String> splitStringToToken(final String string, final String regExp) {
        final Pattern pattern = Pattern.compile(regExp);
        final Matcher matcher = pattern.matcher(string);
        final List<String> token = new ArrayList<>();
        while (matcher.find()) {
            token.add(matcher.group(1));
        }
        return token.size() == 0 ? Collections.EMPTY_LIST : token;
    }

    /**
     * return the file name from the command string
     *
     * @param command
     * @return file name in the command
     */
    public static String getFileName(final String command) {
        return command.substring(command.lastIndexOf(":") + 1);
    }
}
