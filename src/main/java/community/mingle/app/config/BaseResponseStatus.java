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
    EMAIL_FORMAT_ERROR(false, 2011, "이메일 형식을 확인해주세요."),
    USER_EXISTS_EMAIL(false,2012,"이미 존재하는 이메일 주소입니다."),
    EMAIL_CODE_FAIL(false, 2013, "인증번호가 일치하지 않습니다."),
    CODE_EMPTY_ERROR(false, 2019, "인증번호를 입력해주세요."),

    //password
    PASSWORD_EMPTY_ERROR(false, 2014, "비밀번호를 입력해주세요."),
    PASSWORD_LENGTH_ERROR(false, 2015, "비밀번호가 너무 짧습니다."),
    PASSWORD_FORMAT_ERROR(false, 2016, "비밀번호는 영문,숫자를 포함해야 합니다."),
    USER_EXISTS_NICKNAME(false, 2017, "이미 존재하는 닉네임입니다."),
    INVALID_UNIV_ID(false,2018 , "존재하지 않는 학교 id 입니다."),



    /** 2. /member */
    USER_NOT_EXIST(false, 2020, "회원 정보를 찾을 수 없습니다."),
    ALREADY_REPORTED(false, 2021, "이미 신고한 컨텐츠입니다."),
//    DELETE_USER_NOTEXIST(false,2020,"삭제할 유저가 존재하지 않습니다."),
//    POSTS_USERS_EXISTS_NICKNAME(false, 5030, "중복된 닉네임입니다."),
//    DELETE_FAIL_USER(false, 1000, "유저 삭제를 실패하였습니다."),

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
    FAILED_TO_LOGIN(false,3011,"일치하는 이메일이나 비밀번호를 찾지 못했습니다.\n" +
            "띄어쓰기나 잘못된 글자가 있는지 확인해 주세요."),
    FAILED_TO_CREATEJWT(false, 3012, "JWT 발급에 실패하였습니다."),
    FAILED_TO_CHANGEPWD(false, 3013, "비밀번호 변경에 실패하였습니다."),
    FAILED_TO_GET_TERMS(false, 3014, "개인정보처리방침을 가져오는데 실패하였습니다"),
    EMAIL_CODE_EXPIRED(false, 3015, "인증번호 입력시간이 초과되었습니다."),

    USER_MISMATCH_ERROR(false, 3016, "입력하신 정보가 사용자 정보와 맞지 않습니다."),

    USER_DELETED_ERROR(false, 3017,"탈퇴한 사용자입니다."),
    USER_REPORTED_ERROR(false, 3018, "신고된 사용자입니다."),

            /** 2. /member  */
    MODIFY_FAIL_NICKNAME(false, 3020,"닉네임 수정에 실패하였습니다." ),

    //DELETE_FAIL_USER(false,3021,"유저 삭제에 실패했습니다."),
    //MODIFY_FAIL_NICKNAME(false,3022,"닉네임 수정에 실패하였습니다."),

    /** 3. /posts 주석 해제하고 쓰기 */

    MODIFY_FAIL_POST(false, 3020, "게시물 수정을 실패했습니다."),
    TITLE_EMPTY_ERROR(false, 3021, "제목/본문을 입력해주세요."),
    DELETE_FAIL_POST(false, 3025, "게시물 삭제를 실패했습니다."),
    EMPTY_RECENT_POSTS(false, 3029, "최근 올라온 게시글이 없어요."), //홈화면 최신글 api
    EMPTY_BEST_POSTS(false, 3030,"인기 게시물이 없어요."),
    EMPTY_POSTS_LIST(false, 3031, "해당 카테고리에 게시물이 없습니다."),
    INVALID_POST_CATEGORY(false, 3032, "유효하지 않은 카테고리 입니다."),
    CREATE_FAIL_POST(false, 3033, "게시물 생성에 실패하였습니다."),
    EMPTY_MYPOST_LIST(false, 3034, "게시글이 없어요"),
    POST_NOT_EXIST(false, 3035, "게시물이 존재하지 않습니다."),
    REPORTED_DELETED_POST(false, 3036, "삭제되거나 신고된 게시물 입니다."),
    MODIFY_NOT_AUTHORIZED(false, 3040, "게시물 수정 권한이 없습니다."),

    DUPLICATE_LIKE(false, 3060, "이미 좋아요를 눌렀어요."),
    DUPLICATE_SCRAP(false, 3061, "이미 스크랩을 눌렀어요."),
    DELETED_LIKE(false, 3062, "이미 좋아요를 취소했어요."),
    DELETED_SCRAP(false, 3063, "이미 스크랩을 취소했어요."),
    BLIND_NOT_EXIST(false, 3064, "해당 게시물을 가리지 않았어요"),

    DUPLICATE_BLIND(false,3064 ,"이미 게시물을 가렸어요." ),



    /**
     * 이미지 업로드
     */
    UPLOAD_FAIL_IMAGE(false, 3070, "이미지 업로드에 실패했습니다"),
    INVALID_IMAGE_FORMAT(false, 3071,"잘못된 형식의 파일입니다"),
    INVALID_IMAGE(false, 3072,"유효하지 않은 이미지입니다"),
    INVALID_IMAGE_NUMBER(false, 3073, "이미지 개수를 초과하였습니다"),
    DELETE_FAIL_IMAGE(false, 3074,"이미지 삭제에 실패했습니다"),


    /** 4. /comment  */
    DELETE_FAIL_COMMENT(false, 4025, "댓글 삭제를 실패했습니다."),
    COMMENT_NOT_EXIST(false, 4035, "댓글이 존재하지 않습니다."),
    FAILED_TO_CREATECOMMENT(false, 4040, "잘못된 parentCommentId / mentionId 입니다."),
    REPORTED_DELETED_COMMENT(false, 4050, "삭제되거나 신고된 댓글입니다."),




    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),


    // /auth : 암호화
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    EMAIL_ENCRYPTION_ERROR(false, 4012, "이메일 암호화에 실패하였습니다."),
    CODE_GENERATE_FAIL(false, 4013, "인증번호 생성에 실패하습니다."),
    EMAIL_SEND_FAIL(false, 4014, "인증번호 전송에 실패하였습니다.");



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
