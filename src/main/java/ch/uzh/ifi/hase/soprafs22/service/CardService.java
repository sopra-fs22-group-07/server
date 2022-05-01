package ch.uzh.ifi.hase.soprafs22.service;

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;
import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;
import ch.uzh.ifi.hase.soprafs22.repository.WhiteCardRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileReader;


@Service
@Transactional
public class CardService {

    private final Logger log = LoggerFactory.getLogger(CardService.class);

    private final BlackCardRepository blackCardRepository;
    private final WhiteCardRepository whiteCardRepository;

    @Autowired
    public CardService(@Qualifier("BlackCardRepository") BlackCardRepository blackCardRepository, @Qualifier("WhiteCardRepository") WhiteCardRepository whiteCardRepository) {
        this.blackCardRepository = blackCardRepository;
        this.whiteCardRepository = whiteCardRepository;

        // load the data
        this.loadCardData();
    }

    // read the data from the data folder
    public void loadCardData() {

        log.info("Loading card...");

        // parser to parse the file
        JSONParser parser = new JSONParser();

        try{

            String path = "src/main/resources/cah-cards-full.json";
            Object obj = parser.parse(new FileReader(path));

            // cast the object to a JSONArray
            JSONArray jsonArray = (JSONArray) obj;

            // for each pack in the array, extract pack name, black cards, and official tag
            for (Object o : jsonArray) {

                // get the set
                JSONObject pack = (JSONObject) o;
                // get the set name
                String packName = (String) pack.get("name");
                // get the official tag
                Boolean officialTag = (Boolean) pack.get("official");


                // ================== black cards ==================

                // get the black cards
                JSONArray blackCards = (JSONArray) pack.get("black");

                // for each black card, extract the text and add it to the database
                for (Object card : blackCards) {

                    // get the black card
                    JSONObject blackCardJson = (JSONObject) card;

                    // get the text
                    String text = (String) blackCardJson.get("text");

                    // get the pack id
                    Long packIdTemp = (Long) blackCardJson.get("pack");
                    int packID = packIdTemp.intValue();

                    // get number of blanks
                    Long nrOfBlanksTemp = (Long) blackCardJson.get("pick");
                    int nrOfBlanks = nrOfBlanksTemp.intValue();

                    // some cards have very long texts, so we only allow a maximum of 256 characters
                    // otherwise the database will throw an error
                    if ((text.length() <= 255) && (nrOfBlanks == 1)) {

                        // create card object
                        BlackCard blackCard = new BlackCard();
                        blackCard.setText(text);
                        blackCard.setPackName(packName);
                        blackCard.setPackID(packID);
                        blackCard.setOfficialTag(officialTag);

                        // add the card to the database
                        this.blackCardRepository.save(blackCard);
                        this.blackCardRepository.flush();
                    }
                }


                // ================== white cards ==================

                // get the white cards
                JSONArray whiteCards = (JSONArray) pack.get("white");

                // for each white card, extract the text and add it to the database
                for (Object card : whiteCards) {

                    // get the white card
                    JSONObject whiteCardJson = (JSONObject) card;

                    // get the text
                    String text = (String) whiteCardJson.get("text");

                    // get the pack id
                    Long packIdTemp = (Long) whiteCardJson.get("pack");
                    int packID = packIdTemp.intValue();

                    // create card object
                    WhiteCard whiteCard = new WhiteCard();
                    whiteCard.setText(text);
                    whiteCard.setPackName(packName);
                    whiteCard.setPackID(packID);
                    whiteCard.setOfficialTag(officialTag);

                    // add the card to the database
                    this.whiteCardRepository.save(whiteCard);
                    this.whiteCardRepository.flush();
                }

            }

        log.info("All cards loaded.");

        } catch (Exception e) {
            log.error("Error: Could not load data.");
        }
    }
    
}
