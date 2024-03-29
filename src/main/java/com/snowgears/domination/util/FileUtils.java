package com.snowgears.domination.util;

import com.snowgears.domination.Domination;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Utility class used for manipulating files.
 */
public class FileUtils {

    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFolder(File source, File destination)
    {
        if (source.isDirectory())
        {
            if (!destination.exists())
            {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files)
            {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = null;
            OutputStream out = null;

            try
            {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
            @Override public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace(); // replace with more robust error handling
                return FileVisitResult.TERMINATE;
            }

            @Override public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if(e!=null)return handleException(e);
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void createWorldBackup() throws IOException {
        File world_domination = new File(Domination.getPlugin().getServer().getWorldContainer(), Domination.getPlugin().getWorldName());
        File world_domination_backup = new File(Domination.getPlugin().getServer().getWorldContainer(), Domination.getPlugin().getWorldName()+"_backup");
        copyFolder(world_domination, world_domination_backup);
    }

    public static void restoreWorldFromBackup() throws IOException {
        Bukkit.getServer().unloadWorld(Domination.getPlugin().getWorldName(), false);
        System.out.println("Unloaded domination_world.");

        File world_domination = new File(Domination.getPlugin().getServer().getWorldContainer(), Domination.getPlugin().getWorldName());
        File world_domination_backup = new File(Domination.getPlugin().getServer().getWorldContainer(), Domination.getPlugin().getWorldName()+"_backup");
        copyFolder(world_domination_backup, world_domination);
        System.out.println("Copied backup world to domination world.");

        Bukkit.createWorld(new WorldCreator(Domination.getPlugin().getWorldName()));
        //WorldCreator wc = new WorldCreator(Domination.getPlugin().getWorldName());
        //World world = Domination.getPlugin().getServer().createWorld(wc);
        //Domination.getPlugin().getServer().getWorlds().add(world);
        System.out.println("Loaded and added domination world.");
    }
}
