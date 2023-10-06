package com.example.football.service.impl;

import static com.example.football.constant.ConstantMessages.*;

import com.example.football.models.dto.TownImportDto;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtilImpl;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


@Repository
public class TownServiceImpl implements TownService {
    private static final String TOWN_FILE_PATH = "src/main/resources/files/json/towns.json";

    private final TownRepository townRepository;

    private final Gson gson;

    private final ModelMapper modelMapper;

    private final ValidationUtilImpl validationUtil;

    public TownServiceImpl(TownRepository townRepository, Gson gson, ModelMapper modelMapper, ValidationUtilImpl validationUtil) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWN_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(gson.fromJson(readTownsFileContent(), TownImportDto[].class))
                .filter(townImportDto -> {
                    if (!validationUtil.isValid(townImportDto) || isExistByName(townImportDto) ) {
                        sb.append(String.format(INVALID,TOWN)).append(System.lineSeparator());
                        return false;
                    }
                    return true;
                })
                .map(townImportDto -> modelMapper.map(townImportDto, Town.class))
                .forEach(town -> {
                    townRepository.save(town);
                    sb.append(String.format(SUCCESSFULLY_IMPORTED,TOWN,town.getName() + " -",town.getPopulation()))
                            .append(System.lineSeparator());
                });
        return sb.toString().trim();
    }

    private boolean isExistByName(TownImportDto townImportDto) {
        return this.townRepository.existsByName(townImportDto.getName());
    }
}


