/*
 * The MIT License
 *
 * Copyright 2015 Nikolaos Bousios (Rambou).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 *
 * @author Nikolaos Bousios (Rambou)
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] Args) {

        //Checking if at least 1 argument has been given
        if (Args.length < 1) {
            System.exit(0);
        }
        //Checking if the argument is a valid path of Directory
        if (!(new File(Args[0])).isDirectory()) {
            System.exit(-1);
        }

        //Create the mbox file to store mails from txt
        File mboxfile = null;

        try {
            //Checking if user has given the mbox file name
            if (Args.length > 1) {
                mboxfile = new File(Args[1]);
            } else {
                mboxfile = new File("test/All_mails.mbox");

                //If file exist then gets deleted
                if (mboxfile.exists()) {
                    mboxfile.delete();
                }
            }

            //File is created
            mboxfile.createNewFile();
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(-1);
        }

        //Getting all .txt files
        ArrayList<File> Files = listFilesAllFiles(Args[0], new ArrayList<>());

        for (File f : Files) {

            String _from = "From: ",
                    From = "From ",
                    Date = "Date",
                    To = "To: ",
                    Subject = "Subject: ",
                    Body = null,
                    ContentType = "Content-Type: text/plain; charset=utf-8",
                    ContentTransferEncoding = "Content-Transfer-Encoding: base64";

            //Read the txt file first and contruct the message into mbox format
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(f), "UTF8"))) {
                StringBuilder sb = new StringBuilder();
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    if (line.contains(_from)) {
                        _from += line.split(":")[1];
                        From += line.split(":")[1];
                    } else if (line.contains(Date)) {
                        Date = (line.split(":")[1] + ":" + line.split(":")[2]).trim();
                        //Setting the correct format for date
                        DateFormat USER_DF_TIME = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                                DateFormat.SHORT);
                        Date = USER_DF_TIME.parse(Date).toString();
                    } else if (line.contains(To)) {
                        To += line.split(":")[1];
                    } else if (line.contains(Subject)) {
                        Subject += line.split(":")[1];
                    } else {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                    }
                }
                br.close();

                //Convert body in base64 using UTF-8 charset
                Body = Base64.getEncoder().encodeToString(sb.toString().getBytes(StandardCharsets.UTF_8));

            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            //Now write the content at the end of the mbox file
            try {

                OutputStreamWriter writer = new OutputStreamWriter(
                        new FileOutputStream(mboxfile, true));
                BufferedWriter fbw = new BufferedWriter(writer);
                fbw.append(From + " " + Date);
                fbw.newLine();
                fbw.append(_from);
                fbw.newLine();
                fbw.append(To);
                fbw.newLine();
                fbw.append(Subject);
                fbw.newLine();
                fbw.append(ContentType);
                fbw.newLine();
                fbw.append(ContentTransferEncoding);
                fbw.newLine();
                fbw.newLine();
                fbw.append(Body);
                fbw.newLine();
                fbw.newLine();
                fbw.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    //A recursive function to get all files from the given directory and its subdirectories
    public static ArrayList<File> listFilesAllFiles(String directoryName, ArrayList<File> Files) {

        //Getting all files of the Directory
        File Directory = new File(directoryName);
        File[] listOfFiles = Directory.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (isFileTxt(file)) {
                    Files.add(new File(file.getAbsolutePath()));
                }
            } else if (file.isDirectory()) {
                listFilesAllFiles(file.getAbsolutePath(), Files);
            }
        }

        return Files;
    }

    private static boolean isFileTxt(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false; // empty extension
        }
        return name.substring(lastIndexOf).equals(".txt");
    }
}
