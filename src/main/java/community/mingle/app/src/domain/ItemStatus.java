package community.mingle.app.src.domain;

public enum ItemStatus {

//    MOBILE("안드로이드"),
//    WEB("스프링"),
//    SERVER("리눅스");
//
//    final private String name;
//    public String getName() {
//        return name;
//    }
//    private DevType(String name){
//        this.name = name;
//    }
//

    SELLING("판매중"),
    RESERVED("예약중"),
    SOLDOUT("판매완료"),
    INACTIVE("삭제됨"),
    NOTIFIED("신고중"),
    REPORTED("신고됨"),
    DELETED("운영진 삭제");

    private final String name;

    ItemStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


//    SELLING, RESERVED, SOLDOUT, INACTIVE, NOTIFIED, REPORTED
//    ACTIVE, INACTIVE, REPORTED, NOTIFIED, DELETED,


}
