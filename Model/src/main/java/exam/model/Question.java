package exam.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.io.Serializable;

@Entity
@Table(name = "Questions")
public class Question implements Serializable, exam.model.Entity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "difficulty")
    private Integer difficulty;
    @Column(name = "question")
    private String question;
    @Column(name = "answer")
    private String answer;


    public Question(int difficulty, String question, String answer) {
        this.difficulty = difficulty;
        this.question = question;
        this.answer = answer;
    }

    public Question() {
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long aLong) {
        this.id = aLong;
    }


}
