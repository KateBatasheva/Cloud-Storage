package Server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class NioServer {

    private final ServerSocketChannel serverChannel = ServerSocketChannel.open();
    private final Selector selector = Selector.open();
    private final ByteBuffer buffer = ByteBuffer.allocate(5);
    private Path serverPath = Paths.get("");
    private Path currentPath;

    public NioServer() throws IOException {
        currentPath = serverPath;
        serverChannel.bind(new InetSocketAddress(8189));
         System.out.println("Сервер запущен");
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (serverChannel.isOpen()) {
            selector.select(); // block
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer();
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int read = 0;
        StringBuilder msg = new StringBuilder();
        while ((read = channel.read(buffer)) > 0) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get());
            }
            buffer.clear();
        }
        String command = msg.toString().replaceAll("[\n|\r]", "");
        String [] fullCommand = command.split(" ",2);
        if (fullCommand[0].equals(String.valueOf(SystemCommands.ls))) {
            String files = Files.list(currentPath)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.joining(", "));
            files += "\n";
            channel.write(ByteBuffer.wrap(files.getBytes(StandardCharsets.UTF_8)));
        }

        if (fullCommand[0].equals(((SystemCommands.mkdirDirName.getCode())))){
            Path path = Paths.get(fullCommand[1]);
            try {
                Path newDir = Files.createDirectory(Paths.get((currentPath + "/" + path)));
                 System.out.println("Каталог создан");
            } catch(FileAlreadyExistsException e){
                 System.out.println("Каталог существует");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fullCommand[0].equals(String.valueOf((SystemCommands.touchFileName.getCode())))) {
            boolean pathExists = Files.exists(Paths.get((fullCommand[1])));
            if (pathExists){
                 System.out.println("Файл " + fullCommand[1] + " уже существует");
            } else {
                Path file = Files.createFile(Paths.get((currentPath + "/" + fullCommand[1])));
                System.out.println("Файл " + fullCommand[1] + " создан");
            }
        }
        if (fullCommand[0].equals(String.valueOf((SystemCommands.cdPath.getCode())))) {
                Path path = Paths.get(fullCommand[1]);
                if (Files.exists(path)){
                    currentPath = Paths.get(fullCommand[1]);
                     System.out.println("Изменен каталог на " +fullCommand[1]);
                } else {
                     System.out.println("Запрашиваемый каталог не существует");
                }
        }
        if (fullCommand[0].equals(String.valueOf((SystemCommands.catFilePath.getCode())))) {
            StringBuilder sb = new StringBuilder();
            Path path = Paths.get(fullCommand[1]);
            File file = new File(String.valueOf(Paths.get(currentPath + "/" + fullCommand[1])));
            Scanner sc = new Scanner(file);
            while (sc.hasNext()){
                sb.append(sc.nextLine()).append("\n");
            }
            System.out.println(sb);
            channel.write(ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.UTF_8)));

        }
        }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }
}
