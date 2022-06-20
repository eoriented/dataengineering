package com.fastcampus.hadoop;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

// 하둡에서는 객체를 바이트 스트림으로 전환하기 위한 직렬화 과정이 필요한데 이를 위하여 Writable이라는 직렬화 포맷을 제공합니다. 
// 그리고 실습에서는 복합 키를 정의할 것이기 때문에 키의 경우에는 정렬을 위한 comparable 인터페이스도 정의해야 합니다. 
// 그래서그 두가지 인터페이스가 합쳐진 WritableComparable 인터페이스를 구현합니다.
public class TextText implements WritableComparable<TextText> {
    private Text first;
    private Text second;

    public TextText() {
        set(new Text(), new Text());
    }

    public TextText(String first, String second) {
        set(new Text(first), new Text(second));
    }

    public TextText(Text first, Text second) {
        set(first, second);
    }

    public void set(Text first, Text second) {
        this.first = first;
        this.second = second;
    }

    public Text getFirst() {
        return first;
    }

    public Text getSecond() {
        return second;
    }

    @Override
    public int compareTo(TextText o) {
        int cmp = first.compareTo(o.first);
        if (cmp != 0) { // first가 같지 않으면 first 기준
            return cmp;
        }
        // first가 같으면 second 기준
        return second.compareTo(o.second);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        // Text의 write 사용. 순차적으로 직렬화
        first.write(out);
        second.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        // Text의 readFields 사용. 순차적으로 역직렬화
        first.readFields(in);
        second.readFields(in);
    }

    @Override
    public int hashCode() {
        return first.hashCode() * 163 + second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TextText) {
            TextText tp = (TextText) obj;
            return first.equals(tp.first) && second.equals(tp.second);
        }
        return false;
    }

    @Override
    public String toString() {
        return first.toString() + ", " + second.toString();
    }
}
