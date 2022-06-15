package com.example.class_design.dao.MapperImp;
import com.example.class_design.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    public User queryUserByName(String name);
    public boolean exixtUsername(String username);
    public void insert_user(User user);
}
