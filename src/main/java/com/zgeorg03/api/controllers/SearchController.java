package com.zgeorg03.api.controllers;

import com.zgeorg03.api.services.DocumentCollectionService;
import com.zgeorg03.core.SearchResponse;
import com.zgeorg03.exceptions.CollectionNotFound;
import com.zgeorg03.exceptions.DocumentNotExists;
import com.zgeorg03.exceptions.QueryFormatNotValid;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/search")
@Api(tags = "Search Engine", description = "Documents Search Service")
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentCollectionController.class);
    private DocumentCollectionService documentCollectionService;

    @Autowired()
    public void setDocumentCollectionService(DocumentCollectionService documentCollectionService) {
        this.documentCollectionService = documentCollectionService;
    }

    @RequestMapping(value = "/{collection}",method = RequestMethod.GET)
    public SearchResponse query(@PathVariable("collection")String collection, @RequestParam("q") String q){

        try{

            return documentCollectionService.search(collection,q);

        }catch (CollectionNotFound e) {
            logger.info(e.getLocalizedMessage());
            return new SearchResponse("Collection "+collection+" not found");
        } catch (QueryFormatNotValid e) {
            logger.info(e.getLocalizedMessage());
            return new SearchResponse(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.info(e.getLocalizedMessage());
            return new SearchResponse("Something gone wrong with IO");
        } catch (DocumentNotExists e) {
            logger.info(e.getLocalizedMessage());
            return new SearchResponse(e.getLocalizedMessage());
        }

    }
}
