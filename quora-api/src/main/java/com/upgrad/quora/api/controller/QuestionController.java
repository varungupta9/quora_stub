package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private QuestionService questionService;
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> postQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) throws  AuthorizationFailedException,AuthenticationFailedException{
    String accessToken = authenticationService.getBearerAccessToken(authorization);
        //Check if the bearer authentication exists
        UserAuthEntity userAuthEntity = authenticationService.validateBearerAuthentication(accessToken, "to post question");

        UserEntity user = userAuthEntity.getUser();
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUser(user);

        // create question
        questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse,HttpStatus.OK);
    }
}
