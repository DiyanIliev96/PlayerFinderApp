package com.example.football.repository;

import com.example.football.models.dto.PlayerExportDto;
import com.example.football.models.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByEmail(String email);
    @Query("select new com.example.football.models.dto.PlayerExportDto(p.firstName,p.lastName,p.position,p.team.name," +
            "p.team.stadiumName) " +
            "from Player p " +
            "join Team t on p.team = t " +
            "join Stat s on p.stat = s " +
            "where p.birthDate between :startDate and :endDate " +
            "order by p.stat.shooting desc,p.stat.passing desc,p.stat.endurance desc,p.lastName ")
    List<PlayerExportDto> getAllPlayersBirthDateBetween(LocalDate startDate, LocalDate endDate);

}
