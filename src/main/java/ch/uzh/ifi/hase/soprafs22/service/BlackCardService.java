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

import ch.uzh.ifi.hase.soprafs22.entity.BlackCard;
import ch.uzh.ifi.hase.soprafs22.repository.BlackCardRepository;


@Service
@Transactional
public class BlackCardService {


    private final Logger log = LoggerFactory.getLogger(BlackCardService.class);

    private final BlackCardRepository blackCardRepository;

    @Autowired
    public BlackCardService(@Qualifier("blackCardRepository") BlackCardRepository blackCardRepository) {
        this.blackCardRepository = blackCardRepository;
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

            // for each set in the array, extract set name, black cards, and official tag
            for(int i = 0; i < nrSets; i++){

                // get the set
                JSONObject set = (JSONObject) jsonArray.get(i);
                // get the set name
                String setName = (String) set.get("name");
                // get the official tag
                Boolean officialTag = (Boolean) set.get("official");

                // get the black cards
                JSONArray blackCards = (JSONArray) set.get("black");
                int nrBlackCards = blackCards.size();

                // for each black card, extract the text and add it to the database
                for(int j = 0; j < nrBlackCards; j++){

                    // get the black card
                    JSONObject blackCardJson = (JSONObject) blackCards.get(j);

                    // get the text
                    String text = (String) blackCardJson.get("text");

                    // get the pack id
                    int packID = (int) blackCardJson.get("pack");

                    // get number of blanks
                    int blanks = (int) blackCardJson.get("pick");

                    // create card object
                    BlackCard blackCard = new BlackCard(text, setName, packID, officialTag, blanks);

                    // add the card to the database
                    this.blackCardRepository.save(blackCard);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
