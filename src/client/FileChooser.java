package client;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
 
public class FileChooser {
 
    public static String FileChooser(){
        
        String folderPath = "";
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(true);   // Fileter ��� ���� ���� 
        chooser.setDialogTitle("Ÿ��Ʋ"); // â�� ����
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // ���� ���� ���      
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT����", "txt"); // filter Ȯ���� �߰�
        chooser.addChoosableFileFilter(filter); // ���� ���͸� �߰�;
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG����", "png")); // ���� ���͸� �߰�;
        int returnVal = chooser.showOpenDialog(null); // ����� â ����      
        if(returnVal == JFileChooser.APPROVE_OPTION) { // ���⸦ Ŭ�� 
            folderPath = chooser.getSelectedFile().toString();
            System.out.println(folderPath);
        }else if(returnVal == JFileChooser.CANCEL_OPTION){ // ��Ҹ� Ŭ��
            System.out.println("cancel"); 
            folderPath = "";
        }
        return folderPath;
    }
}