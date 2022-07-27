package community.mingle.app.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatusCopy {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    EMAIL_EMPTY_ERROR(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
   USER_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),

//    9주차
    POST_POSTS_INVALID_CONTENTS(false,2018,"내용의 글자수를 확인해주세요."),
    POST_POSTS_EMPTY_IMGURL(false,2019,"게시물의 이미지를 입력해주세요."),//한 게시글에는 이미지가 한개 이상이 있어야함

    POSTS_EMPTY_POST_ID(false,2020,"게시물 아이디 값을 확인해주세요."),
    //10주차 로그인
    POST_USERS_EMPTY_PASSWORD(false,2030,"비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false,2031,"비밀번호 형식을 확인해주세요."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
//    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"존재하지 않는 이메일이거나 비밀번호가 틀렸습니다."),

    //9주차 3.3 게시물수정
    MODIFY_FAIL_POST(false, 3020, "게시물 수정을 실패했습니다."),

    //3.4 게시물삭제
    DELETE_FAIL_POST(false, 3030, "게시물 삭제를 실패했습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    //7주차 챌린지과제 지 유저삭제 Api
    DELETE_FAIL_USER(false,4015,"유저 삭제 실패"),
    DELETE_USER_NOTEXIST(false,4016,"삭제할 유저가 존재하지 않습니다."),


    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    //1.8 회원가입 추가
    EMAIL_ENCRYPTION_ERROR(false, 4020, "이메일 암호화에 실패하였습니다."),
    EMAIL_DECRYPTION_ERROR(false, 4021, "이메일 복호화에 실패하였습니다."),


    /**
     * 1. auth
     */
    //0713 추가

    EMAIL_FORMAT_ERROR(false, 5010, "이메일 형식을 확인해주세요. //프론트에서 확인해주세요"),

    PASSWORD_EMPTY_ERROR(false, 5020, "비밀번호를 입력해주세요."),
    PASSWORD_LENGTH_ERROR(false, 5021, "비밀번호가 너무 짧습니다."),
    PASSWORD_FORMAT_ERROR(false, 5022, "비밀번호는 영문,숫자,특수문자를 포함해야 합니다."),
    PASSWORD_MATCH_ERROR(false, 5023, "비밀번호가 일치하지 않습니다. "),


    POSTS_USERS_EXISTS_NICKNAME(false, 5030, "닉네임 중복");
    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatusCopy(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
