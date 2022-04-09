package ch.uzh.ifi.hase.soprafs22.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.transaction.Transactional;
import java.io.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;
import ch.uzh.ifi.hase.soprafs22.repository.WhiteCardRepository;


@Service
@Transactional
public class WhiteCardService {


    private final Logger log = LoggerFactory.getLogger(WhiteCardService.class);

    private final WhiteCardRepository whiteCardRepository;

    @Autowired
    public WhiteCardService(@Qualifier("whiteCardRepository") WhiteCardRepository whiteCardRepository) {
        this.whiteCardRepository = whiteCardRepository;

        // load the data
        this.loadCardData();
    }


    // read the data from the data folder
    public void loadCardData() {

        System.out.println("Loading white cards...");

        // parser to parse the file
        JSONParser parser = new JSONParser();

        try{

            Object obj = parser.parse(new FileReader("data/cah-cards-full.json"));

            // cast the object to a JSONArray
            JSONArray jsonArray = (JSONArray) obj;
            int nrPacks = jsonArray.size();

            // for each pack in the array, extract pack name, white cards, and official tag
            for(int i = 0; i < nrPacks; i++){

                // get the set
                JSONObject pack = (JSONObject) jsonArray.get(i);
                // get the set name
                String packName = (String) pack.get("name");
                // get the official tag
                Boolean officialTag = (Boolean) pack.get("official");

                // get the white cards
                JSONArray whiteCards = (JSONArray) pack.get("white");
                int nrWhiteCards = whiteCards.size();

                // for each white card, extract the text and add it to the database
                for(int j = 0; j < nrWhiteCards; j++){

                    // get the white card
                    JSONObject whiteCardJson = (JSONObject) whiteCards.get(j);

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

        System.out.println("White cards loaded.");

        } catch (Exception e) {
            System.out.println("Error: Could not load data.");
        }
    }
    
}
