package ua.artstood.ssecomments;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Comment {
    private String user;
    private String content;
}
