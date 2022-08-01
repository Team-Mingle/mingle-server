package community.mingle.app.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 * 당장 usage 가 없는건 주석처리 했습니다.
 * 필요한것들 주석 해제하고 쓰기
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    //REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    //USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),



    /** 1. /auth  */

    //email
    EMAIL_EMPTY_ERROR(false, 2010, "이메일을 입력해주세요."),
    EMAIL_FORMAT_ERROR(false, 2011, "이메일 형식을 확인해주세요.//프론트에서 확인해주세요"),
    USER_EXISTS_EMAIL(false,2012,"중복된 이메일입니다."),
    EMAIL_CODE_FAIL(false, 2013, "인증번호가 일치하지 않습니다."),
    CODE_GENERATE_FAIL(false, 2014, "인증번호 생성에 실패하습니다."),
    EMAIL_SEND_FAIL(false, 2015, "인증번호 전송에 실패하였습니다."),


    //password
    PASSWORD_EMPTY_ERROR(false, 2014, "비밀번호를 입력해주세요."),
    PASSWORD_LENGTH_ERROR(false, 2015, "비밀번호가 너무 짧습니다."),
    PASSWORD_FORMAT_ERROR(false, 2016, "비밀번호는 영문,숫자를 포함해야 합니다."),
    USER_EXISTS_NICKNAME(false, 2017, "중복된 닉네임입니다."),
    INVALID_UNIV_ID(false,2018 , "존재하지 않는 학교 id 입니다."),
    USER_NOT_EXIST(false, 2019, "등록되지 않은 유저입니다."),


    /** 2. /user */
//    DELETE_USER_NOTEXIST(false,2020,"삭제할 유저가 존재하지 않습니다."),
//    POSTS_USERS_EXISTS_NICKNAME(false, 5030, "중복된 닉네임입니다."),


    /** 3. /posts  */
//    POST_INVALID_CONTENTS(false,2030,"내용의 글자수를 확인해주세요."),
//    POST_EMPTY_IMGURL(false,2031,"게시물의 이미지를 입력해주세요."),//한 게시글에는 이미지가 한개 이상이 있어야함
//    POST_EMPTY_POST_ID(false,2032,"게시물 아이디 값을 확인해주세요."),


    /** 4. /comment  */





    /**
     * 3000 : Response 오류
     */
    // Common
    //RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /** 1. /auth  */

    FAILED_TO_SIGNUP(false, 3010, "회원가입에 실패하였습니다."),
    FAILED_TO_LOGIN(false,3011,"존재하지 않는 이메일이거나 비밀번호가 틀렸습니다."),
    FAILED_TO_CREATEJWT(false, 3012, "JWT 발급에 실패하였습니다."),
    FAILED_TO_CHANGEPWD(false, 3013, "비밀번호 변경에 실패하였습니다."),
    FAILED_TO_GET_TERMS(false, 3014, "개인정보처리방침을 가져오는데 실패하였습니다"),

    /** 2. /user  */
    //DELETE_FAIL_USER(false,3020,"유저 삭제에 실패했습니다."),
    //MODIFY_FAIL_NICKNAME(false,3021,"닉네임 수정에 실패하였습니다."),

    /** 3. /posts 주석 해제하고 쓰기 */
    EMPTY_BEST_POSTS(false, 3030,"최근 3일간 올라온 베스트 게시물이 없습니다."),
    EMPTY_POSTS_LIST(false, 3031, "해당 카테고리에 게시물이 없습니다."),
    INVALID_POST_CATEGORY(false, 3032, "유효하지 않은 카테고리 입니다."),
    CREATE_FAIL_POST(false, 3033, "게시물 생성에 실패하였습니다."),

    POST_NOT_EXIST(false, 3035, "게시물이 존재하지 않습니다."),

    MODIFY_NOT_AUTHORIZED(false, 3040, "게시물 수정 권한이 없습니다."),
    MODIFY_FAIL_POST(false, 3020, "게시물 수정을 실패했습니다."),
    TITLE_EMPTY_ERROR(false, 3021, "제목을 입력해주세요."),

    DELETE_FAIL_POST(false, 3025, "게시물 삭제를 실패했습니다."),

    /** 4. /comment  */





    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),


    // /auth : 암호화
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    EMAIL_ENCRYPTION_ERROR(false, 4012, "이메일 암호화에 실패하였습니다.");




    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
