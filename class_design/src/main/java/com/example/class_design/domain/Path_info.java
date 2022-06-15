package com.example.class_design.domain;


public class Path_info {
    private String files1;
    private String files2;
    private boolean isfile;
    private String BlockSize;
    private String path;

    public Path_info(String files1, String files2, boolean isfile, String BlockSize, String path) {
        this.files1 = files1;
        this.files2 = files2;
        this.isfile = isfile;
        this.BlockSize = BlockSize;
        this.path = path;
    }

    public String getFiles1() {
        return files1;
    }

    public void setFiles1(String files1) {
        this.files1 = files1;
    }

    public String getFiles2() {
        return files2;
    }

    public void setFiles2(String files2) {
        this.files2 = files2;
    }

    public boolean isIsfile() {
        return isfile;
    }

    public void setIsfile(boolean isfile) {
        this.isfile = isfile;
    }

    public String getBlockSize() {
        return BlockSize;
    }

    public void setBlockSize(String blockSize) {
        BlockSize = blockSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Path_info{" +
                "files1='" + files1 + '\'' +
                ", files2='" + files2 + '\'' +
                ", isfile=" + isfile +
                ", BlockSize='" + BlockSize + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
