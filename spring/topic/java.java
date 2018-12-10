


#成员变量和属性
	//一般情况下,成员变量和属性名相同.
	public class Flower {
		public String name; //name: 成员变量

		public void setName(String name) { //(setName中的name): 属性名
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
























