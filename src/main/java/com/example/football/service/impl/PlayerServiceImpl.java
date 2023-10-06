package com.example.football.service.impl;

import com.example.football.models.dto.PlayerExportDto;
import com.example.football.models.dto.PlayerImportDto;
import com.example.football.models.dto.PlayerRootDto;
import static com.example.football.constant.ConstantMessages.*;

import com.example.football.models.entity.Player;
import com.example.football.models.entity.Stat;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import com.example.football.util.ValidationUtilImpl;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@Repository
public class PlayerServiceImpl implements PlayerService {
    private static final String PLAYER_FILE_PATH = "src/main/resources/files/xml/players.xml";

    private final PlayerRepository playerRepository;

    private final XmlParser xmlParser;

    private final ValidationUtilImpl validationUtil;

    private final ModelMapper modelMapper;

    private final TownRepository townRepository;

    private final TeamRepository teamRepository;

    private final StatRepository statRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, XmlParser xmlParser, ValidationUtilImpl validationUtil,
                             ModelMapper modelMapper, TownRepository townRepository, TeamRepository teamRepository,
                             StatRepository statRepository) {
        this.playerRepository = playerRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.townRepository = townRepository;
        this.teamRepository = teamRepository;
        this.statRepository = statRepository;
    }


    @Override
    public boolean areImported() {
        return playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYER_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        xmlParser.fromFile(Path.of(PLAYER_FILE_PATH).toFile(), PlayerRootDto.class).getPlayers().stream()
                .filter(playerImportDto -> {
                    if (!validationUtil.isValid(playerImportDto) || isExistByEmail(playerImportDto)) {
                        sb.append(String.format(INVALID,PLAYER)).append(System.lineSeparator());
                        return false;
                    }
                    return true;
                })
                .map(this::mapPlayer)
                .forEach(player -> {
                    playerRepository.save(player);
                    sb.append(String.format(SUCCESSFULLY_IMPORTED,PLAYER,player.getFirstName(),player.getLastName() + " - "))
                            .append(player.getPosition()).append(System.lineSeparator());
                });

        return sb.toString().trim();
    }

    private Player mapPlayer(PlayerImportDto playerImportDto) {
        Player player = modelMapper.map(playerImportDto,Player.class);
        Town town = this.townRepository.findByName(playerImportDto.getTown().getName());
        Team team = this.teamRepository.findByName(playerImportDto.getTeam().getName());
        Stat stat = this.statRepository.getById(playerImportDto.getStat().getId());
        player.setTown(town);
        player.setTeam(team);
        player.setStat(stat);
        return player;
    }

    @Override
    public String exportBestPlayers() {
        LocalDate startDate = LocalDate.of(1995,1,1);
        LocalDate endDate = LocalDate.of(2003,1,1);
        List<PlayerExportDto> allPlayersBirthDateBetween = playerRepository.getAllPlayersBirthDateBetween(startDate,endDate);
        StringBuilder sb = new StringBuilder();
        allPlayersBirthDateBetween.forEach(playerExportDto -> {
            sb.append(playerExportDto);
            sb.append(System.lineSeparator());
        });
        return sb.toString().trim();
    }

    private boolean isExistByEmail(PlayerImportDto playerImportDto) {
        return this.playerRepository.existsByEmail(playerImportDto.getEmail());
    }
}
