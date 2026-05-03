package com.zeter.memedb;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemeRepository extends JpaRepository<Meme, String>{
    
    Meme findMemeById(String Id);

}
