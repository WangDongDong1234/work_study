package meituan;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class TestUnionBlockCodes {
    public static void main(String[] args) {
        System.out.println(unionBlockCodes("abc","def"));
    }

    //锁定码合并    blockCode1="abc" blockcodes2="def"  返回 abcdef
    public static String unionBlockCodes(String blockCodes1,String blockcodes2){
        if(blockCodes1==null){
            blockCodes1="";
        }

        if(blockcodes2==null){
            blockcodes2="";
        }

        char[] a=blockCodes1.toCharArray();
        char[] b=blockcodes2.toCharArray();
        //去重
        Set<Character> set =new LinkedHashSet();
        char[] var9=a;
        int var8=a.length;

        char _b;
        int var7;
        for(var7 = 0;var7<var8;++var7){
            _b=var9[var7];
            set.add(_b);
        }

        var9 =b;
        var8=b.length;
        for(var7 = 0;var7<var8;++var7){
            _b=var9[var7];
            set.add(_b);
        }

        StringBuilder result=new StringBuilder();
        Iterator iterator=set.iterator();
        while (iterator.hasNext()){
            result.append(iterator.next());
        }

        return "".equals(result)?null:result.toString();

    }
}
