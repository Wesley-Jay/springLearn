package com.example.elasticsearch.utils;

/**
 * @author wsj
 * @description 文件类型
 * @date 2023/11/15
 */
public enum FileTypeEnum {
    PPT("1","ppt"),
    PPTX("2","pptx"),
    XLSX("3","xlsx"),
    XLS("4","xls"),
    CSV("5","csv"),
    DOC("6","doc"),
    DOCX("7","docx"),
    PDF("8","pdf"),
    MD("9","md"),
    TXT("10","txt"),
    JPG("11","jpg"),
    PNG("12","png"),
    MP3("13","mp3"),
    AVI("14","avi"),
    LINK("15","link");

    private final String id;
    private final String name;

    FileTypeEnum(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    // 根据name获取id
    public static String getIdByName(String name) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (item.getName().equals(name)) {
                return item.getId();
            }
        }
        throw new RuntimeException("文件枚举没有该类型");
    }

    public static String getNameById(String id) {
        for (FileTypeEnum item : FileTypeEnum.values()) {
            if (item.getId().equals(id)) {
                return item.getName();
            }
        }
        throw new RuntimeException("文件枚举没有该类型");
    }
}
