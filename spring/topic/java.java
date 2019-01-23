


//{--------<<<基础>>>-------------------------------------------------------------------------
#Object.equals()方法
    public boolean equals(Object obj) {
        if (this == obj) //入参对象和当前对象引用一样,则肯定是同一个对象实例, true
            return true;

        if (obj == null) //入参对象为null, false
            return false;

        if (getClass() != obj.getClass()) //入参对象和当前对象类型不一样,肯定不相等, false
            return false;

        Dog other = (Dog) obj; //至此,入参对象和当前对象类型一样,直接强转

        if (age != other.age) //先比较age属性,不一样就返回 false
            return false;

        //再检查name属性, 由于name是字符串,不能直接用==比较.
        //这里之所以没有直接 name.equals(other.name) 是因为如果name为null,会引起NullPointerException
        if (name == null) {
            if (other.name != null) //this.name为null,而入参.name却不为null,肯定不相等, false
                return false;
        } else if (!name.equals(other.name)) //this.name不为null,可直接调用String的equals()判断两个name是否一样
            return false;

        return true; //前面都满足,则说明两个对象相等 true
    }

    public boolean equals1(Object o) { //上述方法简化版
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Dog dog = (Dog) o;
        return age == dog.age &&
                Objects.equals(name, dog.name);
    }

#成员变量和属性
	//一般情况下,成员变量和属性名相同.
	public class Flower {
		public String name; //name: 成员变量

		public void setName(String name) { //setName中的name: 属性名
			this.name = name;
		}
	}
	
#replace - replaceAll - replaceFirst
    //(1).replace()参数是'char'和'CharSequence'; 即支持'字符和字符串'的替换
    //(2).replaceAll()参数是'regex'; 即基于'正则表达式'的替换; ---> 可通过replaceAll("\\d", "*")把所有的数字替换成星号;
    //(3).replaceFirst()参数也是'regex'; 但不同的是只替换第一个,以上是全部替换
    private void doReplace() {
        String src = "aa333";
        System.out.println(src.replace('a', 'f')); //ff333
        System.out.println(src.replace("a", "f")); //ff333
        System.out.println(src.replaceAll("\\d", "f")); //aafff
        System.out.println(src.replaceAll("a", "f")); //ff333
        System.out.println(src.replaceFirst("\\d", "f")); //aaf33
        System.out.println(src.replaceFirst("a", "f")); //fa333
    }

//}

//{--------<<<反射>>>-------------------------------------------------------------------------

//}

//{--------<<<x>>>-------------------------------------------------------------------------

}













