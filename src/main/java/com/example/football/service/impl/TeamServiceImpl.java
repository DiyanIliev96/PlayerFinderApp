package com.example.football.service.impl;

import com.example.football.models.dto.TeamImportDto;
import static com.example.football.constant.ConstantMessages.*;

import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.TeamService;
import com.example.football.util.ValidationUtilImpl;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Repository
public class TeamServiceImpl implements TeamService {

    private static final String TEAM_FILE_PATH = "src/main/resources/files/json/teams.json";

    private final TeamRepository teamRepository;
    private final Gson gson;

    private final ModelMapper modelMapper;

    private final ValidationUtilImpl validationUtil;

    private final TownRepository townRepository;

    public TeamServiceImpl(TeamRepository teamRepository, Gson gson, ModelMapper modelMapper, ValidationUtilImpl validationUtil, TownRepository townRepository) {
        this.teamRepository = teamRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.townRepository = townRepository;
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count() > 0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(Path.of(TEAM_FILE_PATH));
    }

    @Override
    public String importTeams() throws IOException {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(gson.fromJson(readTeamsFileContent(), TeamImportDto[].class))
                .filter(teamImportDto -> {
                    if (!validationUtil.isValid(teamImportDto) || isExistByName(teamImportDto)) {
                        sb.append(String.format(INVALID,TEAM)).append(System.lineSeparator());
                        return false;
                    }
                    return true;
                })
                .map(this::mapTeam)
                .forEach(team -> {
                    this.teamRepository.save(team);
                    sb.append(String.format(SUCCESSFULLY_IMPORTED,TEAM,team.getName() + " -",team.getFanBase()))
                            .append(System.lineSeparator());
                });

        return sb.toString().trim();
    }

    private Team mapTeam(TeamImportDto teamImportDto) {
        Team team = modelMapper.map(teamImportDto,Team.class);
        Town town = this.townRepository.findByName(teamImportDto.getTownName());
        team.setTown(town);
        return team;
    }

    private boolean isExistByName(TeamImportDto teamImportDto) {
        return teamRepository.existsByName(teamImportDto.getName());
    }
}
