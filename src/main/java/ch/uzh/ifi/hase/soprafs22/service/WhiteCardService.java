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
    }


    // read the data from the data folder
    public void loadCardData() {

        // parser to parse the file
        JSONParser parser = new JSONParser();

        try{

            Object obj = parser.parse(new FileReader("data/cah-cards-full.json"));

            // cast the object to a JSONArray
            JSONArray jsonArray = (JSONArray) obj;
            int nrSets = jsonArray.size();

            // for each set in the array, extract set name, white cards, and official tag
            for(int i = 0; i < nrSets; i++){

                // get the set
                JSONObject set = (JSONObject) jsonArray.get(i);
                // get the set name
                String setName = (String) set.get("name");
                // get the official tag
                Boolean officialTag = (Boolean) set.get("official");

                // get the white cards
                JSONArray whiteCards = (JSONArray) set.get("white");
                int nrWhiteCards = whiteCards.size();

                // for each white card, extract the text and add it to the database
                for(int j = 0; j < nrWhiteCards; j++){

                    // get the white card
                    JSONObject whiteCardJson = (JSONObject) whiteCards.get(j);

                    // get the text
                    String text = (String) whiteCardJson.get("text");

                    // get the pack id
                    int packID = (int) whiteCardJson.get("pack");

                    // create card object
                    WhiteCard whiteCard = new WhiteCard(text, setName, packID, officialTag);

                    // add the card to the database
                    this.whiteCardRepository.save(whiteCard);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
