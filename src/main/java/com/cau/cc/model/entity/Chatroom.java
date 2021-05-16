//package com.cau.cc.model.entity;
//
//import lombok.*;
//import lombok.experimental.Accessors;
//
//import javax.persistence.*;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//@Entity
//@Builder
//@Accessors(chain = true)
//@ToString(exclude = {"matchingId"})
//public class Chatroom {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String name;
//
//    @JoinColumn(name = "matching_id")
//    @ManyToOne
//    private Matching matchingId;
//}
package com.cau.cc.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = {"manId","womanId"})
@Accessors(chain = true)
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime time;

    @JoinColumn(name = "man_id")
    @ManyToOne
    private Account manId;

    @JoinColumn(name = "woman_id")
    @ManyToOne
    private Account womanId;


}
