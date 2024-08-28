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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.ericsson.cifwk.taf.utils.FileFinder;

public final class FileOperationHelper {

    /**
     * find the first occurrence of the file extension
     *
     * @param parentDir
     *            the directory to look for the file in
     * @param ext
     *            the file name extension to look for
     * @return a file in the directory with the specified extension
     */
    public static File findByExtension(final String parentDir, final String ext) throws FileNotFoundException {
        final File directory = new File(parentDir);

        File foundFile = null;
        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException(parentDir + "not found!");
        }
        for (final File f : directory.listFiles()) {
            if (f.getName().endsWith("." + ext)) {
                foundFile = f;
                break;
            }
        }

        return foundFile;
    }

    /**
     * Read file.
     *
     * @param path
     *            the path
     * @param encoding
     *            the encoding
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String readFile(final String path, final Charset encoding) throws IOException {
        final byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /**
     * Read file from class path
     *
     * @param path
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFileFromClassPath(final String path, final Charset encoding) throws IOException {
        final StringBuffer sb = new StringBuffer();
        final BufferedReader in = new BufferedReader(new InputStreamReader(FileOperationHelper.class.getClassLoader().getResourceAsStream(path)));
        String line = null;
        while ((line = in.readLine()) != null) {
            sb.append(line);
        }
        return new String(sb.toString().getBytes(), encoding);
    }

    public static void extractFileFromArchiveToDirectory(final String archiveLocation, final String extractDirectory, final String fileName) {
        ZipFile zipFile;
        ZipEntry zipEntry = null;
        try {
            zipFile = new ZipFile(archiveLocation);
            final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            while (zipEntries.hasMoreElements()) {
                zipEntry = zipEntries.nextElement();
                final String entryName = zipEntry.getName();
                if (entryName.equals(fileName)) {
                    final File file = new File(extractDirectory + entryName);

                    final InputStream inputStream = zipFile.getInputStream(zipEntry);
                    final FileOutputStream fileOutputStream = new FileOutputStream(file);
                    final byte[] bytes = new byte[1024];
                    int length;
                    while ((length = inputStream.read(bytes)) >= 0) {
                        fileOutputStream.write(bytes, 0, length);
                    }
                    inputStream.close();
                    fileOutputStream.close();
                }
            }
            zipFile.close();
        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * @param fileName
     *            file name for loading
     * @return the file from file finder
     */
    public static File getFileFromFileFinder(final String fileName) {
        File file = null;
        final Iterator<String> it = FileFinder.findFile(fileName).iterator();
        while (it.hasNext()) {
            final String strfileName = it.next();

            final Path p = Paths.get(strfileName);
            if (p.getFileName().toString().equals(fileName)) {
                file = new File(strfileName);

                // I usually have only 1 <fileName> in the list
                // but if I run TAF in a local environment I have multiple
                // instances
                // In this case I return the one present under target directory
                if (strfileName.contains("target")) {
                    break;
                }
            }

        }
        return file;
    }

}
