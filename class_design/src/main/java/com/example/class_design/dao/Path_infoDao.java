package com.example.class_design.dao;
import com.example.class_design.domain.Path_info;
import java.util.ArrayList;
import java.util.List;
public class Path_infoDao {
    private List<Path_info> path_infos = null;

    public Path_infoDao() {
        this.path_infos = new ArrayList<>();
    }

    public List<Path_info> getPath_infos() {
        return path_infos;
    }

    public void setPath_infos(List<Path_info> path_infos) {
        this.path_infos = path_infos;
    }

    @Override
    public String toString() {
        return "Path_infoDao{" +
                "path_infos=" + path_infos +
                '}';
    }
}
