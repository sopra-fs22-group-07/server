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
import ch.uzh.ifi.hase.soprafs22.entity.WhiteCard;
import ch.uzh.ifi.hase.soprafs22.repository.CardRepository;


@Service
@Transactional
public class CardService {


    private final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository CardRepository;

    @Autowired
    public CardService(@Qualifier("CardRepository") CardRepository CardRepository) {
        this.CardRepository = CardRepository;

        // load the data
        this.loadCardData();
    }

    // read the data from the data folder
    public void loadCardData() {

        System.out.println("Loading cards...");

        // parser to parse the file
        JSONParser parser = new JSONParser();

        try{

            Object obj = parser.parse(new FileReader("data/cah-cards-full.json"));

            // cast the object to a JSONArray
            JSONArray jsonArray = (JSONArray) obj;
            int nrPacks = jsonArray.size();

            // for each pack in the array, extract pack name, black cards, and official tag
            for(int i = 0; i < nrPacks; i++){

                // get the set
                JSONObject pack = (JSONObject) jsonArray.get(i);
                // get the set name
                String packName = (String) pack.get("name");
                // get the official tag
                Boolean officialTag = (Boolean) pack.get("official");


                // ================== black cards ==================

                // get the black cards
                JSONArray blackCards = (JSONArray) pack.get("black");
                int nrBlackCards = blackCards.size();

                // for each black card, extract the text and add it to the database
                for(int j = 0; j < nrBlackCards; j++){

                    // get the black card
                    JSONObject blackCardJson = (JSONObject) blackCards.get(j);

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
                    if (text.length() <= 255) {

                        // create card object
                        BlackCard blackCard = new BlackCard();
                        blackCard.setText(text);
                        blackCard.setPackName(packName);
                        blackCard.setPackID(packID);
                        blackCard.setOfficialTag(officialTag);
                        blackCard.setNrOfBlanks(nrOfBlanks);

                        // add the card to the database
                        this.CardRepository.save(blackCard);
                        this.CardRepository.flush();
                    } 
                }


                // ================== white cards ==================

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
                    this.CardRepository.save(whiteCard);
                    this.CardRepository.flush();
                }

            }

        System.out.println("All cards loaded.");

        } catch (Exception e) {
            System.out.println("Error: Could not load data.");
        }
    }
    
}
