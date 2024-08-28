/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.operators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.ericsson.oss.services.scriptengine.spi.dtos.AbstractDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.HeaderRowDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.LineDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.ResponseDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowCell;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowDto;

/**
 * Class to Correctly Parse the contents of the CommandResponseDto object from ScriptEngine
 *
 * Class contains an array of the AbstractDtos obtained from the 'elements' List<AbstractDto>
 * in the ResponseDto from Script Engine
 */
public class ResponseDtoWrapper {

    AbstractDto[] abstractDtos;

    ResponseDtoWrapper(final AbstractDto[] abstractDtos){
        this.abstractDtos=abstractDtos;
    }


    public Map<String,String> fetchErrorAndSuggestSolutionForSingleNodeErrorScenarioFromResponse(){

        final Map<String,String> errorAndSuggestedSolution = new HashMap();

        for (int i=0; i< abstractDtos.length;i++) {

            if (abstractDtos[i] instanceof LineDto) {

                final LineDto lineDto = (LineDto) abstractDtos[i];
                if(lineDto.getValue() != null && lineDto.getValue().startsWith("Error")){
                   // System.out.println("lineDTO Value : " + lineDto.getValue());
                    errorAndSuggestedSolution.put("Error", lineDto.getValue());
                }
                if(lineDto.getValue() != null && lineDto.getValue().startsWith("Suggested Solution")) {
                    //System.out.println("lineDTO Value : " + lineDto.getValue());
                    errorAndSuggestedSolution.put("SuggestedSolution",lineDto.getValue());
                }
            }
        }
        System.out.println("DTOLength= " + abstractDtos.length);
        if (abstractDtos.length!=3 && abstractDtos.length!=6){
            throw new RuntimeException("Response with no Error/Suggested Solution encountered or more lines than expected encountered:"
                    +  "or worse The Format of Response in the CM components has changed and we need to update our code! ");
        }
        return errorAndSuggestedSolution;
    }

    public Map<String,List<String>> fetchErrorAndSuggestSolutionForMultipleNodeErrorScenarioFromResponse(){

        final List<List<String>> list = new ArrayList();
        final Map<String,List<String>> map = new HashMap();

        for (int i=0; i< abstractDtos.length;i++) {

            if (abstractDtos[i] instanceof RowDto && !(abstractDtos[i] instanceof HeaderRowDto)) {

                final RowDto rowDto = (RowDto) abstractDtos[i];
                final List<String> nodeList = new ArrayList();

                for (final RowCell cell : rowDto.getElements()){
                    nodeList.add(cell.getValue());
                }
                list.add(nodeList);

                final String nodeName = rowDto.getElements().get(0).getValue();
                map.put(nodeName,nodeList);

            }
        }
        return map;
    }


    public String fetchSingleAttributeValueForAttributeNameFromResponse(final String attributeName){

        //find the line DTO with attributeName
        // The next the value will follow separated by a :

        for (int i=0; i< abstractDtos.length;i++) {

            if (!(abstractDtos[i] instanceof LineDto)) { return null; }
            final LineDto lineDto = (LineDto) abstractDtos[i];
            if(lineDto.getValue() != null && lineDto.getValue().contains(attributeName))
            {
                final String[] attributeNameAttributeValueArray = lineDto.getValue().split(":");
                System.out.println("\n Attribute " + attributeName +" returned is : " + attributeNameAttributeValueArray[1] );
                return attributeNameAttributeValueArray[1].trim();
            }
        }
        System.out.println("\n Attribute " + attributeName + " not found ");
        return "";
    }

    // TODO Done needs to be tested (not used yet)
    public Map<String,String> fetchAllFdnsAndAttributeValuesForASingleAttributeFromResponse(final String attributeName){

        //find the Array Position of AbstractDTO with attributeName in the value
        // The next position in the array wil hold an Abstract DTO with the value

        final Map fdnAttributeValueMap = new HashMap<>();

        for (int i=0; i< abstractDtos.length;i++) {

            if (!(abstractDtos[i] instanceof LineDto)) { return null; }
            LineDto lineDto = (LineDto) abstractDtos[i];
            if(lineDto.getValue() != null && lineDto.getValue().contains(attributeName))
            {
                final String[] nameArray = lineDto.getValue().split(":");
                final String attributeValue = nameArray[1];

                lineDto=(LineDto) abstractDtos[i-1];
                final String[] fdnArray = lineDto.getValue().split(":");
                final String fdn = fdnArray[1];
              //  System.out.println("\n Value Of fdn and attribute value returned is : " + fdn + " : " + attributeValue);

                fdnAttributeValueMap.put(fdn,attributeValue);
            }
        }
        return fdnAttributeValueMap;
    }

    public Set<String> fetchFdnOfMosFromResponseFromCmeditMoQuery(){

        final Set fdnSet = new HashSet();
        for(final AbstractDto dto : abstractDtos){
            if  ( dto instanceof LineDto) {
                final LineDto lineDto = (LineDto) dto;
                final String value = lineDto.getValue();
                if(lineDto.getValue() != null && value.contains("FDN")){
                    final String[] nameArray = value.split(":");
                    final String fdn = nameArray[1];
                    fdnSet.add(fdn);
                }
            }
        }
        return fdnSet;
    }


    /**
     * checks if any of the AbstractDtos contain the String passed into this method.
     * @param entry
     * @return
     */
    public boolean containsString(final String entry){

        for (final AbstractDto dto : abstractDtos) {
            if ( dto instanceof LineDto) {
                final LineDto lineDto = (LineDto) dto;
                // if(lineDto.getValue() != null && lineDto.getValue().equalsIgnoreCase(entry))
                if(lineDto.getValue() != null && lineDto.getValue().toLowerCase().contains(entry.toLowerCase()))
                {
                    return true;
                }

            }else if (dto instanceof RowDto){
                final RowDto rowDto = (RowDto) dto;
                for (final RowCell cell : rowDto.getElements()){
                    if(cell.getValue().equals(entry)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks the number of valid rows in an AbstractDto
     * If the AbstractDto is of LineDto type, the number of lines are counted until a null 'value' is met
     * If the AbstractDto is of OrderedTableDto type, the number of rows in the table is returned
     * @return
     */
    public int countRows(){
        int lineCounter=0;
        int rowCounter=-1;

        for (final AbstractDto dto : abstractDtos) {
            if ( dto instanceof LineDto) {
                final LineDto lineDto = (LineDto) dto;
                if(lineDto.getValue() != null) {
                    lineCounter++;
                }
            }
            else if ( dto instanceof RowDto) {
                final RowDto rowDto = (RowDto) dto;
                System.out.println("Row detail;s :"+ rowDto.toString() );
                rowCounter++;
            }
        }
        if(rowCounter>0){
            return rowCounter;
        }else {
            return lineCounter;
        }
    }

    //TODO This is not generic handling of json, very speicific, make generic and rename
    public Map<String,String> getNodeNamesAndSecurityLevelsFromSecadmSecurityLevelGetCommand(){
        final Map<String,String> namesAndLevelsMap = new HashMap();
        String nodeName= "";
        String nodeSecurityLevel ="";

        System.out.println("In getNodeNamesAndSecurityLevelsFromSecadmSecurityLevelGetCommand");

        for(final AbstractDto dto : abstractDtos){
            if ( dto instanceof RowDto && !(dto instanceof HeaderRowDto)) {

                final RowDto rowDto = (RowDto) dto;

                nodeName = rowDto.getElements().get(0).getValue();

                nodeSecurityLevel = rowDto.getElements().get(1).getValue();

            }
            if(nodeName!="" || nodeSecurityLevel!=""){
                namesAndLevelsMap.put(nodeName,nodeSecurityLevel);
            }
        }

        printMap(namesAndLevelsMap,"secadmnamesAndLevelsMap");
        return namesAndLevelsMap;
    }


    private void printMap(final Map map, final String mapName){

        System.out.println("printing Map" + mapName);

        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry mapEntry = (Map.Entry)it.next();
            System.out.println(mapEntry.getKey() + " : " + mapEntry.getValue());
        }
    }



    /**
     * Node names and Security Levels are now returned in 'LineDtos' of the format:
     * "dtoType": "line",
     * "value": "BLUESKY_ERBS05 : LEVEL_1",
     * "weight": 1
     * Checks each LineDto value and if it contains a LEVEL_(1/2/3) Char sequence it splits the String, and enters the node name and level into the
     * map
     *
     * @return Map<String, String> of node names (key) and security level (value)
     */
    public Map getNodeNamesAndSecurityLevels(){
        final Map namesAndLevels = new HashMap();
        String nodeName= "";
        String nodeSecurityLevel ="";

        for(final AbstractDto dto : abstractDtos){
            if  ( dto instanceof LineDto) {
                final LineDto lineDto = (LineDto) dto;
                final String value = lineDto.getValue();
                if(lineDto.getValue() != null && value.contains("FDN")){
                    final String[] nameArray = value.split(":");
                    nodeName = nameArray[1];
                }
                if(lineDto.getValue() != null && value.contains("operationalSecurityLevel")){
                    final String[] levelArray = value.split(":");
                    nodeSecurityLevel = levelArray[1];
                }
            }
            namesAndLevels.put(nodeName,nodeSecurityLevel);
        }
        return namesAndLevels;
    }


    public static ResponseDtoWrapper newResponseDtoWrapper(final String response) {
        final ObjectMapper mapper = new ObjectMapper();

        ResponseDtoWrapper responseDtoWrapper = null;
        ResponseDto responseDto = null;
        try {
            final JsonNode result = mapper.readTree(response);
            final JsonNode dtoResult = result.get("responseDto");

            responseDto = mapper.readValue(dtoResult, ResponseDto.class);
            // System.out.println("Type of DTOs included in the ResponseDTO : Elements " +responseDto.getElements() );
            //responseDtoWrapper = new ResponseDtoWrapper(responseDto.getElements().toArray(new WeightedDto[]{}));
            responseDtoWrapper = new ResponseDtoWrapper(responseDto.getElements().toArray(new AbstractDto[]{}));

        } catch (final IOException e) {
            e.printStackTrace();
        }
        return responseDtoWrapper;
    }

    public Map<String, Map<String, String>> getNodeNamesAndStatus() {
        final Map<String, Map<String, String>> namesAndLevelsMap = new HashMap<>();
        for (final AbstractDto dto : abstractDtos) {
            if (dto instanceof RowDto && !(dto instanceof HeaderRowDto)) {
                final RowDto rowDto = (RowDto) dto;
                final String nodeName = rowDto.getElements().get(0).getValue();
                String ipSecOM = rowDto.getElements().get(1).getValue();
                String ipsecTraffic = rowDto.getElements().get(2).getValue();
                final Map<String, String> value = new HashMap<>(1);
                if(StringUtils.isBlank(ipSecOM)){
                    ipSecOM = "UNKNOWN";
                }if(StringUtils.isBlank(ipsecTraffic)){
                    ipsecTraffic = "UNKNOWN";
                }
                value.put(ipSecOM, ipsecTraffic);
                namesAndLevelsMap.put(nodeName, value);
				/*System.out.println("NodeNames And Status : " + nodeName + " "
						+ ipSecOM + " " + ipsecTraffic);*/
            }
        }
        return namesAndLevelsMap;
    }

}