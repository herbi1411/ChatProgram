package client;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
 
public class FileChooser {
 
    public static String FileChooser(){
        
        String folderPath = "";
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setAcceptAllFileFilterUsed(true);   // Fileter 모든 파일 적용 
        chooser.setDialogTitle("타이틀"); // 창의 제목
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES); // 파일 선택 모드      
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT파일", "txt"); // filter 확장자 추가
        chooser.addChoosableFileFilter(filter); // 파일 필터를 추가;
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG파일", "png")); // 파일 필터를 추가;
        int returnVal = chooser.showOpenDialog(null); // 열기용 창 오픈      
        if(returnVal == JFileChooser.APPROVE_OPTION) { // 열기를 클릭 
            folderPath = chooser.getSelectedFile().toString();
            System.out.println(folderPath);
        }else if(returnVal == JFileChooser.CANCEL_OPTION){ // 취소를 클릭
            System.out.println("cancel"); 
            folderPath = "";
        }
        return folderPath;
    }
}