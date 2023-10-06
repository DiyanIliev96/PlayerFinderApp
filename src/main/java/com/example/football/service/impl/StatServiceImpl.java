package com.example.football.service.impl;

import com.example.football.models.dto.StatImportDto;
import com.example.football.models.dto.StatRootDto;
import static com.example.football.constant.ConstantMessages.*;

import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtilImpl;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
public class StatServiceImpl implements StatService {
    private static final String STAT_FILE_PATH = "src/main/resources/files/xml/stats.xml";
    private final StatRepository statRepository;

    private final XmlParser xmlParser;

    private final ModelMapper modelMapper;

    private final ValidationUtilImpl validationUtil;

    public StatServiceImpl(StatRepository statRepository, XmlParser xmlParser, ModelMapper modelMapper, ValidationUtilImpl validationUtil) {
        this.statRepository = statRepository;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return this.statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STAT_FILE_PATH));
    }

    @Override
    public String importStats() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();

        xmlParser.fromFile(Path.of(STAT_FILE_PATH).toFile(), StatRootDto.class).getStats().stream()
                .filter(statImportDto -> {
                    if (!validationUtil.isValid(statImportDto) || isStatExist(statImportDto)) {
                        sb.append(String.format(INVALID,STAT)).append(System.lineSeparator());
                        return false;
                    }
                    return true;
                })
                .map(statImportDto -> modelMapper.map(statImportDto, Stat.class))
                .forEach(stat -> {
                    statRepository.save(stat);
                    sb.append(String.format(SUCCESSFULLY_IMPORTED,STAT,stat.getShooting() + " -",
                            stat.getPassing() + " - "))
                            .append(String.format("%s",stat.getEndurance()))
                            .append(System.lineSeparator());
                });

        return sb.toString().trim();
    }

    private boolean isStatExist(StatImportDto statImportDto) {
        return this.statRepository.existsByPassingAndShootingAndEndurance(statImportDto.getPassing(),
                statImportDto.getShooting(),statImportDto.getEndurance());
    }
}
