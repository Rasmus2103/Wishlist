package com.example.wishlist.Repositories;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("wishlist_DB")
public class RepositoryDB implements IRepositoryDB {

    @Value("${spring.datasource.url}")
    private String db_url;

    @Value("${spring.datasource.username}")
    private String uid;

    @Value("${spring.datasource.password}")
    private String pwd;

    private String SQL;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement ps;
    private Connection con;

    public Connection connection() {
        try {
            con = DriverManager.getConnection(db_url,uid,pwd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }

    public List<SuperHero> Heroes() {
        List<SuperHero> herolist = new ArrayList<>();
        SuperHero hero;
        try {
            SQL = "SELECT realname, heroname, creationyear, id FROM superhero";
            stmt = connection().createStatement();
            rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                hero = new SuperHero(rs.getString("realname"), rs.getString("heroname"), rs.getInt("creationyear"), rs.getInt("id"));
                herolist.add(hero);
            }
            return herolist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
