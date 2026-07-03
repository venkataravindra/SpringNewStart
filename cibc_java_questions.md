# 📚 **Java Core & Java 8+ Complete Reference Guide**

## 🗂️ **Table of Contents**
1. [HashMap vs ConcurrentHashMap](#1-hashmap-vs-concurrenthashmap)
2. [HashMap Internal Working](#2-hashmap-internal-working)
3. [ArrayList vs LinkedList](#3-arraylist-vs-linkedlist)
4. [equals() and hashCode()](#4-equals-and-hashcode)
5. [Comparable vs Comparator](#5-comparable-vs-comparator)
6. [Functional Interfaces](#6-functional-interfaces)
7. [Stream API](#7-stream-api)
8. [map() vs flatMap()](#8-map-vs-flatmap)
9. [Optional in Java 8](#9-optional-in-java-8)
10. [Object Creation in Memory](#10-object-creation-in-memory)

---

## **1. HashMap vs ConcurrentHashMap**

### **HashMap**
```java
import java.util.HashMap;
import java.util.Map;

public class HashMapExample {
    public static void main(String[] args) {
        Map<String, Integer> hashMap = new HashMap<>();
        
        // Basic operations
        hashMap.put("apple", 10);
        hashMap.put("banana", 20);
        hashMap.put("orange", 15);
        
        System.out.println("HashMap: " + hashMap);
        System.out.println("Value for 'apple': " + hashMap.get("apple"));
    }
}
```

### **ConcurrentHashMap**
```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentHashMapExample {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        
        // Thread-safe operations
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Multiple threads can safely access
        for (int i = 0; i < 10; i++) {
            final int value = i;
            executor.submit(() -> {
                concurrentMap.put("key" + value, value);
                System.out.println("Thread " + Thread.currentThread().getName() + 
                                 " added: key" + value);
            });
        }
        
        executor.shutdown();
    }
}
```

### **Key Differences:**

| Feature | HashMap | ConcurrentHashMap |
|---------|---------|-------------------|
| **Thread Safety** | Not thread-safe | Thread-safe |
| **Synchronization** | No synchronization | Segment-based locking (Java 7), CAS operations (Java 8+) |
| **Performance** | Faster in single-threaded | Slower due to synchronization overhead |
| **Null Values** | Allows one null key and multiple null values | Does not allow null keys or values |
| **Iteration** | Fail-fast iterator | Fail-safe iterator |
| **Memory Overhead** | Lower | Higher due to synchronization structures |

### **When to Use:**
- **HashMap**: Single-threaded applications or when external synchronization is handled
- **ConcurrentHashMap**: Multi-threaded applications requiring thread-safe operations

---

## **2. HashMap Internal Working**

### **Internal Structure**
```java
// Simplified internal structure of HashMap
public class HashMapInternals {
    
    // Internal Node structure
    static class Node<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        
        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    // Array of buckets
    Node<K,V>[] table;
    int size;
    int threshold;
    float loadFactor = 0.75f;
    
    // Hash function
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    // Index calculation
    static int indexFor(int hash, int length) {
        return hash & (length - 1);
    }
}
```

### **Step-by-Step Working:**

#### **1. PUT Operation**
```java
public class HashMapPutExample {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        
        // Step-by-step PUT operation
        map.put("John", 25);
        
        /*
         * Internal Process:
         * 1. Calculate hash: hash("John") = hashCode("John") ^ (hashCode("John") >>> 16)
         * 2. Find bucket index: index = hash & (table.length - 1)
         * 3. Check if bucket is empty:
         *    - If empty: Create new node and place it
         *    - If not empty: Handle collision
         * 4. If collision occurs:
         *    - Check if key already exists (using equals())
         *    - If exists: Update value
         *    - If not exists: Add to chain (linked list) or tree (if > 8 nodes)
         * 5. Check load factor and resize if needed
         */
    }
}
```

#### **2. GET Operation**
```java
public class HashMapGetExample {
    public static void main(String[] args) {
        Map<String, Integer> map = new HashMap<>();
        map.put("John", 25);
        map.put("Jane", 30);
        
        Integer age = map.get("John");
        
        /*
         * Internal Process:
         * 1. Calculate hash of "John"
         * 2. Find bucket index using hash
         * 3. Search in the bucket:
         *    - If single node: Check key equality
         *    - If linked list: Traverse and check each node
         *    - If tree: Use tree search (O(log n))
         * 4. Return value if found, null otherwise
         */
    }
}
```

### **Collision Handling:**
```java
public class CollisionExample {
    // Custom class to demonstrate collision
    static class CustomKey {
        private String key;
        
        public CustomKey(String key) {
            this.key = key;
        }
        
        @Override
        public int hashCode() {
            // Intentionally return same hash to create collision
            return 1;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CustomKey customKey = (CustomKey) obj;
            return Objects.equals(key, customKey.key);
        }
        
        @Override
        public String toString() {
            return key;
        }
    }
    
    public static void main(String[] args) {
        Map<CustomKey, String> map = new HashMap<>();
        
        // All these will hash to same bucket, creating collision
        map.put(new CustomKey("key1"), "value1");
        map.put(new CustomKey("key2"), "value2");
        map.put(new CustomKey("key3"), "value3");
        
        // HashMap will handle collision using chaining
        System.out.println("Map: " + map);
    }
}
```

### **Load Factor and Resizing:**
```java
public class LoadFactorExample {
    public static void main(String[] args) {
        // Default capacity = 16, load factor = 0.75
        Map<Integer, String> map = new HashMap<>();
        
        // Threshold = capacity * load factor = 16 * 0.75 = 12
        // When size exceeds 12, HashMap will resize to 32
        
        for (int i = 0; i < 20; i++) {
            map.put(i, "value" + i);
            System.out.println("Size: " + map.size());
            // Resize happens when size > threshold
        }
    }
}
```

---

## **3. ArrayList vs LinkedList**

### **ArrayList Implementation**
```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListExample {
    public static void main(String[] args) {
        List<String> arrayList = new ArrayList<>();
        
        // Adding elements
        arrayList.add("Apple");     // O(1) amortized
        arrayList.add("Banana");
        arrayList.add("Cherry");
        
        // Random access
        String fruit = arrayList.get(1);  // O(1) - Direct index access
        System.out.println("Fruit at index 1: " + fruit);
        
        // Insertion at specific position
        arrayList.add(1, "Blueberry");  // O(n) - Shifting required
        
        // Removal
        arrayList.remove(0);  // O(n) - Shifting required
        
        System.out.println("ArrayList: " + arrayList);
    }
}
```

### **LinkedList Implementation**
```java
import java.util.LinkedList;
import java.util.List;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<String> linkedList = new LinkedList<>();
        
        // Adding elements
        linkedList.add("Apple");        // O(1)
        linkedList.addFirst("Banana");  // O(1)
        linkedList.addLast("Cherry");   // O(1)
        
        // Access by index
        String fruit = linkedList.get(1);  // O(n) - Sequential traversal
        System.out.println("Fruit at index 1: " + fruit);
        
        // Insertion at beginning/end
        linkedList.addFirst("Blueberry");  // O(1)
        
        // Removal from beginning/end
        linkedList.removeFirst();  // O(1)
        linkedList.removeLast();   // O(1)
        
        System.out.println("LinkedList: " + linkedList);
    }
}
```

### **Performance Comparison:**
```java
import java.util.*;

public class PerformanceComparison {
    public static void main(String[] args) {
        int size = 100000;
        
        // ArrayList performance test
        List<Integer> arrayList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        
        // Add elements
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
        }
        
        // Random access
        for (int i = 0; i < 1000; i++) {
            arrayList.get(i);
        }
        
        long arrayListTime = System.currentTimeMillis() - startTime;
        
        // LinkedList performance test
        List<Integer> linkedList = new LinkedList<>();
        startTime = System.currentTimeMillis();
        
        // Add elements
        for (int i = 0; i < size; i++) {
            linkedList.add(i);
        }
        
        // Sequential access (better for LinkedList)
        Iterator<Integer> iterator = linkedList.iterator();
        int count = 0;
        while (iterator.hasNext() && count < 1000) {
            iterator.next();
            count++;
        }
        
        long linkedListTime = System.currentTimeMillis() - startTime;
        
        System.out.println("ArrayList time: " + arrayListTime + "ms");
        System.out.println("LinkedList time: " + linkedListTime + "ms");
    }
}
```

### **Key Differences:**

| Feature | ArrayList | LinkedList |
|---------|-----------|------------|
| **Data Structure** | Dynamic array | Doubly linked list |
| **Random Access** | O(1) | O(n) |
| **Insertion at end** | O(1) amortized | O(1) |
| **Insertion at beginning** | O(n) | O(1) |
| **Insertion at middle** | O(n) | O(n) |
| **Deletion at end** | O(1) | O(1) |
| **Deletion at beginning** | O(n) | O(1) |
| **Memory Overhead** | Lower | Higher (extra pointers) |
| **Cache Performance** | Better (contiguous memory) | Worse (scattered nodes) |

### **When to Use:**
- **ArrayList**: When you need frequent random access, more reads than writes
- **LinkedList**: When you need frequent insertions/deletions at beginning/end

---

## **4. equals() and hashCode()**

### **Default Implementation**
```java
public class DefaultBehaviorExample {
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        // Using default equals() and hashCode()
    }
    
    public static void main(String[] args) {
        Person p1 = new Person("John", 25);
        Person p2 = new Person("John", 25);
        
        // Default equals() uses reference equality
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // false
        System.out.println("p1 == p2: " + (p1 == p2)); // false
        
        // Default hashCode() returns different values
        System.out.println("p1.hashCode(): " + p1.hashCode());
        System.out.println("p2.hashCode(): " + p2.hashCode());
    }
}
```

### **Proper Implementation**
```java
import java.util.Objects;

public class ProperImplementationExample {
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public boolean equals(Object obj) {
            // 1. Check if same reference
            if (this == obj) return true;
            
            // 2. Check if null or different class
            if (obj == null || getClass() != obj.getClass()) return false;
            
            // 3. Cast and compare fields
            Person person = (Person) obj;
            return age == person.age && Objects.equals(name, person.name);
        }
        
        @Override
        public int hashCode() {
            // Use Objects.hash() for consistent hashing
            return Objects.hash(name, age);
        }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
    
    public static void main(String[] args) {
        Person p1 = new Person("John", 25);
        Person p2 = new Person("John", 25);
        Person p3 = new Person("Jane", 30);
        
        // Proper equals() implementation
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true
        System.out.println("p1.equals(p3): " + p1.equals(p3)); // false
        
        // Consistent hashCode()
        System.out.println("p1.hashCode(): " + p1.hashCode());
        System.out.println("p2.hashCode(): " + p2.hashCode()); // Same as p1
        System.out.println("p3.hashCode(): " + p3.hashCode()); // Different
    }
}
```


### **HashMap Usage Example**
```java
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashMapUsageExample {
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person person = (Person) obj;
            return age == person.age && Objects.equals(name, person.name);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
    
    public static void main(String[] args) {
        Map<Person, String> personMap = new HashMap<>();
        
        Person john1 = new Person("John", 25);
        Person john2 = new Person("John", 25);
        
        personMap.put(john1, "Employee ID: 123");
        
        // This works because of proper equals() and hashCode()
        String employeeId = personMap.get(john2);
        System.out.println("Employee ID: " + employeeId); // Employee ID: 123
        
        // Demonstrates that logically equal objects work as same key
        System.out.println("Contains john2: " + personMap.containsKey(john2)); // true
    }
}
```

### **Contract Rules:**

#### **equals() Contract:**
1. **Reflexive**: `x.equals(x)` must return `true`
2. **Symmetric**: If `x.equals(y)` returns `true`, then `y.equals(x)` must return `true`
3. **Transitive**: If `x.equals(y)` and `y.equals(z)` return `true`, then `x.equals(z)` must return `true`
4. **Consistent**: Multiple invocations must return same result
5. **Null handling**: `x.equals(null)` must return `false`

#### **hashCode() Contract:**
1. **Consistency**: Multiple invocations must return same value
2. **Equality**: If `x.equals(y)` is `true`, then `x.hashCode() == y.hashCode()`
3. **Inequality**: If `x.equals(y)` is `false`, hashCodes should preferably be different

### **Common Mistakes:**
```java
public class CommonMistakes {
    // MISTAKE 1: Override equals() but not hashCode()
    static class BadPerson {
        private String name;
        
        public BadPerson(String name) {
            this.name = name;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BadPerson that = (BadPerson) obj;
            return Objects.equals(name, that.name);
        }
        
        // Missing hashCode() override - VIOLATION!
    }
    
    // MISTAKE 2: Using mutable fields in hashCode()
    static class MutablePerson {
        private String name; // mutable field
        
        public MutablePerson(String name) {
            this.name = name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MutablePerson that = (MutablePerson) obj;
            return Objects.equals(name, that.name);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name); // PROBLEM: uses mutable field
        }
    }
    
    public static void main(String[] args) {
        // Demonstrating the mutable field problem
        Map<MutablePerson, String> map = new HashMap<>();
        MutablePerson person = new MutablePerson("John");
        
        map.put(person, "Value");
        System.out.println("Before name change: " + map.get(person)); // Value
        
        person.setName("Jane"); // Changes hashCode!
        System.out.println("After name change: " + map.get(person)); // null (lost!)
    }
}
```

---

## **5. Comparable vs Comparator**

### **Comparable Interface**
```java
import java.util.*;

// Comparable provides natural ordering
class Student implements Comparable<Student> {
    private String name;
    private int age;
    private double gpa;
    
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    // Natural ordering by GPA (descending)
    @Override
    public int compareTo(Student other) {
        return Double.compare(other.gpa, this.gpa); // Descending order
    }
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getGpa() { return gpa; }
    
    @Override
    public String toString() {
        return String.format("Student{name='%s', age=%d, gpa=%.2f}", name, age, gpa);
    }
}

public class ComparableExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 20, 3.8),
            new Student("Bob", 22, 3.5),
            new Student("Charlie", 21, 3.9),
            new Student("Diana", 19, 3.7)
        );
        
        System.out.println("Before sorting:");
        students.forEach(System.out::println);
        
        // Natural ordering (by GPA descending)
        Collections.sort(students);
        
        System.out.println("\nAfter sorting (by GPA descending):");
        students.forEach(System.out::println);
        
        // Using in TreeSet (automatic sorting)
        Set<Student> studentSet = new TreeSet<>(students);
        System.out.println("\nTreeSet (automatically sorted):");
        studentSet.forEach(System.out::println);
    }
}
```

### **Comparator Interface**
```java
import java.util.*;
import java.util.stream.Collectors;

public class ComparatorExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 20, 3.8),
            new Student("Bob", 22, 3.5),
            new Student("Charlie", 21, 3.9),
            new Student("Diana", 19, 3.7)
        );
        
        // 1. Sort by name (ascending)
        students.sort(Comparator.comparing(Student::getName));
        System.out.println("Sorted by name:");
        students.forEach(System.out::println);
        
        // 2. Sort by age (descending)
        students.sort(Comparator.comparing(Student::getAge).reversed());
        System.out.println("\nSorted by age (descending):");
        students.forEach(System.out::println);
        
        // 3. Multiple criteria sorting
        students.sort(
            Comparator.comparing(Student::getGpa).reversed()
                     .thenComparing(Student::getName)
        );
        System.out.println("\nSorted by GPA (desc) then by name:");
        students.forEach(System.out::println);
        
        // 4. Custom Comparator with lambda
        students.sort((s1, s2) -> {
            int gpaCompare = Double.compare(s2.getGpa(), s1.getGpa());
            if (gpaCompare != 0) return gpaCompare;
            return Integer.compare(s1.getAge(), s2.getAge());
        });
        System.out.println("\nCustom sorting (GPA desc, then age asc):");
        students.forEach(System.out::println);
    }
}
```

### **Advanced Comparator Examples**
```java
import java.util.*;
import java.util.function.Function;

public class AdvancedComparatorExample {
    
    static class Employee {
        private String name;
        private String department;
        private int salary;
        private LocalDate joinDate;
        
        public Employee(String name, String department, int salary, LocalDate joinDate) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.joinDate = joinDate;
        }
        
        // Getters
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public int getSalary() { return salary; }
        public LocalDate getJoinDate() { return joinDate; }
        
        @Override
        public String toString() {
            return String.format("Employee{name='%s', dept='%s', salary=%d, joinDate=%s}", 
                               name, department, salary, joinDate);
        }
    }
    
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("John", "IT", 75000, LocalDate.of(2020, 1, 15)),
            new Employee("Alice", "HR", 65000, LocalDate.of(2019, 3, 10)),
            new Employee("Bob", "IT", 80000, LocalDate.of(2021, 6, 20)),
            new Employee("Carol", "Finance", 70000, LocalDate.of(2020, 8, 5)),
            new Employee("David", "IT", 75000, LocalDate.of(2018, 11, 30))
        );
        
        // 1. Null-safe comparator
        Comparator<Employee> nullSafeNameComparator = 
            Comparator.nullsLast(Comparator.comparing(Employee::getName));
        
        // 2. Complex multi-level sorting
        Comparator<Employee> complexComparator = 
            Comparator.comparing(Employee::getDepartment)
                     .thenComparing(Employee::getSalary, Comparator.reverseOrder())
                     .thenComparing(Employee::getJoinDate);
        
        employees.sort(complexComparator);
        System.out.println("Complex sorting (dept asc, salary desc, joinDate asc):");
        employees.forEach(System.out::println);
        
        // 3. Custom key extractor
        Function<Employee, String> keyExtractor = emp -> 
            emp.getDepartment() + "_" + emp.getSalary();
        
        employees.sort(Comparator.comparing(keyExtractor));
        System.out.println("\nCustom key sorting:");
        employees.forEach(System.out::println);
        
        // 4. Using Comparator in Stream operations
        Optional<Employee> highestPaidIT = employees.stream()
            .filter(emp -> "IT".equals(emp.getDepartment()))
            .max(Comparator.comparing(Employee::getSalary));
        
        System.out.println("\nHighest paid IT employee: " + highestPaidIT.orElse(null));
        
        // 5. Top N employees by salary
        List<Employee> top3BySalary = employees.stream()
            .sorted(Comparator.comparing(Employee::getSalary).reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("\nTop 3 employees by salary:");
        top3BySalary.forEach(System.out::println);
    }
}
```

### **Key Differences:**

| Feature | Comparable | Comparator |
|---------|------------|------------|
| **Package** | java.lang | java.util |
| **Method** | compareTo(T o) | compare(T o1, T o2) |
| **Sorting Logic** | Inside the class (natural ordering) | Outside the class (custom ordering) |
| **Implementation** | Class implements Comparable | Separate class or lambda |
| **Flexibility** | Single sorting sequence | Multiple sorting sequences |
| **Modification** | Requires class modification | No class modification needed |
| **Usage** | Collections.sort(list) | Collections.sort(list, comparator) |

---

## **6. Functional Interfaces**

### **Built-in Functional Interfaces**

#### **1. Predicate<T>**
```java
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Basic Predicate
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> isGreaterThan5 = n -> n > 5;
        
        // Using Predicate
        List<Integer> evenNumbers = numbers.stream()
            .filter(isEven)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // Combining Predicates
        Predicate<Integer> evenAndGreaterThan5 = isEven.and(isGreaterThan5);
        List<Integer> result = numbers.stream()
            .filter(evenAndGreaterThan5)
            .collect(Collectors.toList());
        System.out.println("Even and > 5: " + result);
        
        // Predicate methods: and(), or(), negate()
        Predicate<Integer> oddOrLessThan5 = isEven.negate().or(n -> n < 5);
        List<Integer> result2 = numbers.stream()
            .filter(oddOrLessThan5)
            .collect(Collectors.toList());
        System.out.println("Odd or < 5: " + result2);
        
        // Static method: isEqual()
        Predicate<String> isHello = Predicate.isEqual("Hello");
        System.out.println("Is 'Hello': " + isHello.test("Hello")); // true
        System.out.println("Is 'World': " + isHello.test("World")); // false
    }
}
```


#### **2. Function<T, R> **
```java
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FunctionExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("john doe", "alice smith", "bob jones");
        
        // Function composition: compose()
        Function<String, String> removeSpaces = s -> s.replace(" ", "");
        Function<String, String> toUpperCase = String::toUpperCase;
        
        // compose() applies the parameter function first, then this function
        Function<String, String> upperCaseNoSpaces = toUpperCase.compose(removeSpaces);
        
        List<String> result = names.stream()
            .map(upperCaseNoSpaces)
            .collect(Collectors.toList());
        System.out.println("Uppercase without spaces: " + result);
        
        // Identity function
        Function<String, String> identity = Function.identity();
        List<String> unchanged = names.stream()
            .map(identity)
            .collect(Collectors.toList());
        System.out.println("Identity result: " + unchanged);
        
        // Complex transformation
        Function<String, Map<String, Object>> createPersonMap = name -> {
            String[] parts = name.split(" ");
            Map<String, Object> person = new HashMap<>();
            person.put("firstName", parts[0]);
            person.put("lastName", parts.length > 1 ? parts[1] : "");
            person.put("fullName", name);
            person.put("nameLength", name.length());
            return person;
        };
        
        List<Map<String, Object>> personMaps = names.stream()
            .map(createPersonMap)
            .collect(Collectors.toList());
        System.out.println("Person maps: " + personMaps);
    }
}
```

#### **3. Consumer<T>**
```java
import java.util.*;
import java.util.function.Consumer;

public class ConsumerExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("John", "Alice", "Bob", "Charlie");
        
        // Basic Consumer
        Consumer<String> printName = System.out::println;
        Consumer<String> printLength = name -> System.out.println(name + " has " + name.length() + " characters");
        
        // Using Consumer
        System.out.println("Names:");
        names.forEach(printName);
        
        // Consumer chaining with andThen()
        Consumer<String> printNameAndLength = printName.andThen(printLength);
        System.out.println("\nNames with lengths:");
        names.forEach(printNameAndLength);
        
        // Complex Consumer example
        List<StringBuilder> builders = Arrays.asList(
            new StringBuilder("Hello"),
            new StringBuilder("World"),
            new StringBuilder("Java")
        );
        
        Consumer<StringBuilder> appendExclamation = sb -> sb.append("!");
        Consumer<StringBuilder> makeUpperCase = sb -> {
            String str = sb.toString().toUpperCase();
            sb.setLength(0);
            sb.append(str);
        };
        Consumer<StringBuilder> addPrefix = sb -> sb.insert(0, ">> ");
        
        // Chain multiple operations
        Consumer<StringBuilder> complexOperation = appendExclamation
            .andThen(makeUpperCase)
            .andThen(addPrefix);
        
        builders.forEach(complexOperation);
        System.out.println("\nProcessed builders:");
        builders.forEach(System.out::println);
    }
}
```

#### **4. Supplier<T>**
```java
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class SupplierExample {
    public static void main(String[] args) {
        // Basic Supplier
        Supplier<String> stringSupplier = () -> "Hello World";
        Supplier<Double> randomSupplier = Math::random;
        Supplier<Date> dateSupplier = Date::new;
        
        System.out.println("String: " + stringSupplier.get());
        System.out.println("Random: " + randomSupplier.get());
        System.out.println("Date: " + dateSupplier.get());
        
        // Supplier with Stream.generate()
        List<Integer> randomNumbers = Stream.generate(() -> (int)(Math.random() * 100))
            .limit(5)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println("Random numbers: " + randomNumbers);
        
        // Lazy evaluation with Supplier
        Supplier<String> expensiveOperation = () -> {
            System.out.println("Performing expensive operation...");
            try {
                Thread.sleep(1000); // Simulate expensive operation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Expensive result";
        };
        
        // The operation is not executed until get() is called
        System.out.println("Supplier created, but not executed yet");
        String result = expensiveOperation.get(); // Now it executes
        System.out.println("Result: " + result);
        
        // Factory pattern with Supplier
        Supplier<List<String>> listFactory = ArrayList::new;
        Supplier<Set<String>> setFactory = HashSet::new;
        Supplier<Map<String, String>> mapFactory = HashMap::new;
        
        List<String> list = listFactory.get();
        Set<String> set = setFactory.get();
        Map<String, String> map = mapFactory.get();
        
        System.out.println("Created: " + list.getClass().getSimpleName());
        System.out.println("Created: " + set.getClass().getSimpleName());
        System.out.println("Created: " + map.getClass().getSimpleName());
    }
}
```

### **Custom Functional Interfaces**
```java
@FunctionalInterface
interface Calculator {
    double calculate(double a, double b);
    
    // Default methods are allowed
    default double square(double a) {
        return calculate(a, a);
    }
    
    // Static methods are allowed
    static double abs(double a) {
        return a < 0 ? -a : a;
    }
}

@FunctionalInterface
interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}

@FunctionalInterface
interface StringProcessor {
    String process(String input);
    
    default StringProcessor andThen(StringProcessor after) {
        return input -> after.process(this.process(input));
    }
}

public class CustomFunctionalInterfaceExample {
    public static void main(String[] args) {
        // Using Calculator
        Calculator addition = (a, b) -> a + b;
        Calculator multiplication = (a, b) -> a * b;
        Calculator division = (a, b) -> b != 0 ? a / b : 0;
        
        System.out.println("Addition: " + addition.calculate(10, 5));
        System.out.println("Multiplication: " + multiplication.calculate(10, 5));
        System.out.println("Division: " + division.calculate(10, 5));
        System.out.println("Square: " + addition.square(5));
        System.out.println("Absolute: " + Calculator.abs(-10));
        
        // Using TriFunction
        TriFunction<String, String, String, String> concatenateThree = 
            (a, b, c) -> a + " " + b + " " + c;
        
        String result = concatenateThree.apply("Hello", "Beautiful", "World");
        System.out.println("Concatenated: " + result);
        
        // Using StringProcessor with method chaining
        StringProcessor toUpperCase = String::toUpperCase;
        StringProcessor addExclamation = s -> s + "!";
        StringProcessor addPrefix = s -> ">> " + s;
        
        StringProcessor chainedProcessor = toUpperCase
            .andThen(addExclamation)
            .andThen(addPrefix);
        
        String processed = chainedProcessor.process("hello world");
        System.out.println("Processed: " + processed);
    }
}
```

### **Method References**
```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        
        public static int compareByAge(Person p1, Person p2) {
            return Integer.compare(p1.age, p2.age);
        }
        
        public void printInfo() {
            System.out.println("Person: " + name + ", Age: " + age);
        }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25),
            new Person("Bob", 30),
            new Person("Charlie", 20)
        );
        
        // 1. Static method reference
        people.sort(Person::compareByAge);
        System.out.println("Sorted by age: " + people);
        
        // 2. Instance method reference of a particular object
        Person alice = people.get(0);
        Supplier<String> nameSupplier = alice::getName;
        System.out.println("Alice's name: " + nameSupplier.get());
        
        // 3. Instance method reference of an arbitrary object
        Function<Person, String> nameExtractor = Person::getName;
        List<String> names = people.stream()
            .map(nameExtractor)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        System.out.println("Names: " + names);
        
        // 4. Constructor reference
        BiFunction<String, Integer, Person> personFactory = Person::new;
        Person newPerson = personFactory.apply("David", 35);
        System.out.println("New person: " + newPerson);
        
        // 5. Array constructor reference
        Function<Integer, Person[]> arrayFactory = Person[]::new;
        Person[] personArray = arrayFactory.apply(3);
        System.out.println("Array length: " + personArray.length);
        
        // 6. Method reference vs Lambda comparison
        // These are equivalent:
        Consumer<Person> lambda = person -> person.printInfo();
        Consumer<Person> methodRef = Person::printInfo;
        
        System.out.println("\nUsing lambda:");
        people.forEach(lambda);
        
        System.out.println("\nUsing method reference:");
        people.forEach(methodRef);
    }
}
```

---

## **7. Stream API**

### **Basic Stream Operations**
```java
import java.util.*;
import java.util.stream.*;

public class BasicStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. Filter - Intermediate operation
        List<Integer> evenNumbers = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        System.out.println("Even numbers: " + evenNumbers);
        
        // 2. Map - Transform elements
        List<Integer> squares = numbers.stream()
            .map(n -> n * n)
            .collect(Collectors.toList());
        System.out.println("Squares: " + squares);
        
        // 3. Reduce - Terminal operation
        Optional<Integer> sum = numbers.stream()
            .reduce((a, b) -> a + b);
        System.out.println("Sum: " + sum.orElse(0));
        
        // 4. forEach - Terminal operation
        System.out.print("Numbers: ");
        numbers.stream()
            .filter(n -> n > 5)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 5. Chaining operations
        List<String> result = numbers.stream()
            .filter(n -> n % 2 == 0)           // Keep even numbers
            .map(n -> n * n)                   // Square them
            .filter(n -> n > 10)               // Keep squares > 10
            .map(n -> "Number: " + n)          // Convert to string
            .collect(Collectors.toList());     // Collect to list
        
        System.out.println("Chained operations result: " + result);
    }
}
```

### **Stream Creation Methods**
```java
import java.util.*;
import java.util.stream.*;
import java.nio.file.*;
import java.io.IOException;

public class StreamCreationExample {
    public static void main(String[] args) {
        // 1. From Collections
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        
        // 2. From Arrays
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        
        // 3. Using Stream.of()
        Stream<String> streamOf = Stream.of("hello", "world", "java");
        
        // 4. Empty Stream
        Stream<String> emptyStream = Stream.empty();
        
        // 5. Infinite Streams
        // Generate
        Stream<Double> randomNumbers = Stream.generate(Math::random);
        List<Double> first5Random = randomNumbers.limit(5).collect(Collectors.toList());
        System.out.println("Random numbers: " + first5Random);
        
        // Iterate
        Stream<Integer> evenNumbers = Stream.iterate(0, n -> n + 2);
        List<Integer> first10Even = evenNumbers.limit(10).collect(Collectors.toList());
        System.out.println("Even numbers: " + first10Even);
        
        // 6. Range Streams
        IntStream range = IntStream.range(1, 6); // 1 to 5
        IntStream rangeClosed = IntStream.rangeClosed(1, 5); // 1 to 5 inclusive
        
        System.out.println("Range: " + range.boxed().collect(Collectors.toList()));
        System.out.println("Range closed: " + rangeClosed.boxed().collect(Collectors.toList()));
        
        // 7. From String
        IntStream charStream = "Hello".chars();
        List<Character> chars = charStream
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());
        System.out.println("Characters: " + chars);
        
        // 8. Parallel Stream
        Stream<String> parallelStream = list.parallelStream();
        
        // 9. Builder pattern
        Stream<String> builtStream = Stream.<String>builder()
            .add("a")
            .add("b")
            .add("c")
            .build();
        
        System.out.println("Built stream: " + 
            builtStream.collect(Collectors.toList()));
    }
}
```
## **7. Stream API **

### **Intermediate Operations **
```java
import java.util.*;
import java.util.stream.*;

public class IntermediateOperationsExample {
    
    static class Person {
        private String name;
        private int age;
        private String city;
        private List<String> hobbies;
        
        public Person(String name, int age, String city, List<String> hobbies) {
            this.name = name;
            this.age = age;
            this.city = city;
            this.hobbies = hobbies;
        }
        
        // Getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getCity() { return city; }
        public List<String> getHobbies() { return hobbies; }
        
        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, city='%s', hobbies=%s}", 
                               name, age, city, hobbies);
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25, "New York", Arrays.asList("reading", "swimming")),
            new Person("Bob", 30, "London", Arrays.asList("gaming", "cooking")),
            new Person("Charlie", 25, "New York", Arrays.asList("reading", "gaming")),
            new Person("Diana", 35, "Paris", Arrays.asList("swimming", "traveling")),
            new Person("Eve", 28, "London", Arrays.asList("cooking", "reading"))
        );
        
        // 1. filter() - Keep elements that match predicate
        List<Person> youngPeople = people.stream()
            .filter(p -> p.getAge() < 30)
            .collect(Collectors.toList());
        System.out.println("Young people: " + youngPeople);
        
        // 2. map() - Transform elements
        List<String> names = people.stream()
            .map(Person::getName)
            .collect(Collectors.toList());
        System.out.println("Names: " + names);
        
        // 3. flatMap() - Flatten nested structures
        List<String> allHobbies = people.stream()
            .flatMap(p -> p.getHobbies().stream())
            .collect(Collectors.toList());
        System.out.println("All hobbies: " + allHobbies);
        
        // 4. distinct() - Remove duplicates
        List<String> uniqueHobbies = people.stream()
            .flatMap(p -> p.getHobbies().stream())
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Unique hobbies: " + uniqueHobbies);
        
        // 5. sorted() - Sort elements
        List<Person> sortedByAge = people.stream()
            .sorted(Comparator.comparing(Person::getAge))
            .collect(Collectors.toList());
        System.out.println("Sorted by age: " + sortedByAge);
        
        // 6. limit() - Limit number of elements
        List<Person> first3 = people.stream()
            .limit(3)
            .collect(Collectors.toList());
        System.out.println("First 3: " + first3);
        
        // 7. skip() - Skip first n elements
        List<Person> skipFirst2 = people.stream()
            .skip(2)
            .collect(Collectors.toList());
        System.out.println("Skip first 2: " + skipFirst2);
        
        // 8. peek() - Perform action without modifying stream
        List<String> processedNames = people.stream()
            .peek(p -> System.out.println("Processing: " + p.getName()))
            .map(Person::getName)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("Processed names: " + processedNames);
        
        // 9. Complex chaining
        List<String> result = people.stream()
            .filter(p -> p.getAge() > 25)                    // Adults only
            .filter(p -> p.getCity().equals("London") || 
                        p.getCity().equals("New York"))      // Specific cities
            .flatMap(p -> p.getHobbies().stream())           // Get all hobbies
            .distinct()                                      // Remove duplicates
            .sorted()                                        // Sort alphabetically
            .map(String::toUpperCase)                        // Convert to uppercase
            .collect(Collectors.toList());
        
        System.out.println("Complex result: " + result);
    }
}
```

### **Terminal Operations**
```java
import java.util.*;
import java.util.stream.*;

public class TerminalOperationsExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        
        // 1. collect() - Collect to various collections
        List<Integer> evenList = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
        
        Set<Integer> evenSet = numbers.stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toSet());
        
        String joinedWords = words.stream()
            .collect(Collectors.joining(", "));
        
        System.out.println("Even list: " + evenList);
        System.out.println("Even set: " + evenSet);
        System.out.println("Joined words: " + joinedWords);
        
        // 2. reduce() - Reduce to single value
        Optional<Integer> sum = numbers.stream()
            .reduce((a, b) -> a + b);
        
        Integer sumWithIdentity = numbers.stream()
            .reduce(0, (a, b) -> a + b);
        
        Optional<Integer> max = numbers.stream()
            .reduce(Integer::max);
        
        System.out.println("Sum: " + sum.orElse(0));
        System.out.println("Sum with identity: " + sumWithIdentity);
        System.out.println("Max: " + max.orElse(0));
        
        // 3. forEach() - Perform action on each element
        System.out.print("Numbers: ");
        numbers.stream()
            .filter(n -> n > 5)
            .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 4. count() - Count elements
        long evenCount = numbers.stream()
            .filter(n -> n % 2 == 0)
            .count();
        System.out.println("Even count: " + evenCount);
        
        // 5. anyMatch(), allMatch(), noneMatch()
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        
        boolean allPositive = numbers.stream()
            .allMatch(n -> n > 0);
        
        boolean noneNegative = numbers.stream()
            .noneMatch(n -> n < 0);
        
        System.out.println("Has even: " + hasEven);
        System.out.println("All positive: " + allPositive);
        System.out.println("None negative: " + noneNegative);
        
        // 6. findFirst(), findAny()
        Optional<Integer> firstEven = numbers.stream()
            .filter(n -> n % 2 == 0)
            .findFirst();
        
        Optional<String> anyLongWord = words.stream()
            .filter(w -> w.length() > 5)
            .findAny();
        
        System.out.println("First even: " + firstEven.orElse(-1));
        System.out.println("Any long word: " + anyLongWord.orElse("none"));
        
        // 7. min(), max()
        Optional<Integer> min = numbers.stream()
            .min(Integer::compareTo);
        
        Optional<String> longestWord = words.stream()
            .max(Comparator.comparing(String::length));
        
        System.out.println("Min: " + min.orElse(-1));
        System.out.println("Longest word: " + longestWord.orElse("none"));
        
        // 8. toArray()
        Integer[] evenArray = numbers.stream()
            .filter(n -> n % 2 == 0)
            .toArray(Integer[]::new);
        
        System.out.println("Even array: " + Arrays.toString(evenArray));
    }
}
```

### **Advanced Stream Operations**
```java
import java.util.*;
import java.util.stream.*;
import java.util.function.Function;

public class AdvancedStreamExample {
    
    static class Employee {
        private String name;
        private String department;
        private int salary;
        private int age;
        
        public Employee(String name, String department, int salary, int age) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.age = age;
        }
        
        // Getters
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public int getSalary() { return salary; }
        public int getAge() { return age; }
        
        @Override
        public String toString() {
            return String.format("Employee{name='%s', dept='%s', salary=%d, age=%d}", 
                               name, department, salary, age);
        }
    }
    
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", "IT", 75000, 28),
            new Employee("Bob", "HR", 65000, 32),
            new Employee("Charlie", "IT", 80000, 25),
            new Employee("Diana", "Finance", 70000, 30),
            new Employee("Eve", "IT", 85000, 35),
            new Employee("Frank", "HR", 60000, 29)
        );
        
        // 1. Grouping by department
        Map<String, List<Employee>> byDepartment = employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment));
        
        System.out.println("Grouped by department:");
        byDepartment.forEach((dept, empList) -> {
            System.out.println(dept + ": " + empList.size() + " employees");
        });
        
        // 2. Grouping and counting
        Map<String, Long> countByDepartment = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.counting()
            ));
        System.out.println("Count by department: " + countByDepartment);
        
        // 3. Grouping and calculating average salary
        Map<String, Double> avgSalaryByDept = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.averagingInt(Employee::getSalary)
            ));
        System.out.println("Average salary by department: " + avgSalaryByDept);
        
        // 4. Partitioning (special case of grouping with boolean)
        Map<Boolean, List<Employee>> partitionedByAge = employees.stream()
            .collect(Collectors.partitioningBy(emp -> emp.getAge() > 30));
        
        System.out.println("Employees > 30: " + partitionedByAge.get(true).size());
        System.out.println("Employees <= 30: " + partitionedByAge.get(false).size());
        
        // 5. Complex grouping - Multi-level
        Map<String, Map<Boolean, List<Employee>>> complexGrouping = employees.stream()
            .collect(Collectors.groupingBy(
                Employee::getDepartment,
                Collectors.partitioningBy(emp -> emp.getSalary() > 70000)
            ));
        
        System.out.println("Complex grouping:");
        complexGrouping.forEach((dept, salaryMap) -> {
            System.out.println(dept + ":");
            System.out.println("  High salary: " + salaryMap.get(true).size());
            System.out.println("  Low salary: " + salaryMap.get(false).size());
        });
        
        // 6. Custom collector
        String employeeNames = employees.stream()
            .map(Employee::getName)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("Employee names: " + employeeNames);
        
        // 7. Statistics
        IntSummaryStatistics salaryStats = employees.stream()
            .collect(Collectors.summarizingInt(Employee::getSalary));
        
        System.out.println("Salary statistics:");
        System.out.println("  Count: " + salaryStats.getCount());
        System.out.println("  Sum: " + salaryStats.getSum());
        System.out.println("  Average: " + salaryStats.getAverage());
        System.out.println("  Min: " + salaryStats.getMin());
        System.out.println("  Max: " + salaryStats.getMax());
        
        // 8. Top N employees by salary
        List<Employee> top3BySalary = employees.stream()
            .sorted(Comparator.comparing(Employee::getSalary).reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        System.out.println("Top 3 by salary: " + top3BySalary);
        
        // 9. Department with highest average salary
        Optional<Map.Entry<String, Double>> highestAvgSalaryDept = avgSalaryByDept.entrySet()
            .stream()
            .max(Map.Entry.comparingByValue());
        
        highestAvgSalaryDept.ifPresent(entry -> 
            System.out.println("Highest avg salary dept: " + entry.getKey() + 
                             " (" + entry.getValue() + ")"));
    }
}
```

### **Parallel Streams **
```java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.ForkJoinPool;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> largeList = IntStream.rangeClosed(1, 1000000)
            .boxed()
            .collect(Collectors.toList());
        
        // Sequential vs Parallel comparison
        long startTime, endTime;
        
        // Sequential processing
        startTime = System.currentTimeMillis();
        long sequentialSum = largeList.stream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * n)
            .sum();
        endTime = System.currentTimeMillis();
        System.out.println("Sequential sum: " + sequentialSum + 
                          " (Time: " + (endTime - startTime) + "ms)");
        
        // Parallel processing
        startTime = System.currentTimeMillis();
        long parallelSum = largeList.parallelStream()
            .filter(n -> n % 2 == 0)
            .mapToLong(n -> n * n)
            .sum();
        endTime = System.currentTimeMillis();
        System.out.println("Parallel sum: " + parallelSum + 
                          " (Time: " + (endTime - startTime) + "ms)");
        
        // Converting between sequential and parallel
        Stream<Integer> sequentialStream = largeList.stream();
        Stream<Integer> parallelFromSequential = sequentialStream.parallel();
        
        Stream<Integer> parallelStream = largeList.parallelStream();
        Stream<Integer> sequentialFromParallel = parallelStream.sequential();
        
        // Check if stream is parallel
        System.out.println("Is parallel: " + largeList.parallelStream().isParallel());
        System.out.println("Is sequential: " + largeList.stream().isParallel());
        
        // Custom thread pool for parallel streams
        ForkJoinPool customThreadPool = new ForkJoinPool(4);
        try {
            long customPoolSum = customThreadPool.submit(() ->
                largeList.parallelStream()
                    .filter(n -> n % 2 == 0)
                    .mapToLong(n -> n * n)
                    .sum()
            ).get();
            System.out.println("Custom pool sum: " + customPoolSum);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customThreadPool.shutdown();
        }
        
        // Parallel stream considerations
        demonstrateParallelStreamPitfalls();
    }
    
    private static void demonstrateParallelStreamPitfalls() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // WRONG: Using non-thread-safe operations
        List<Integer> wrongResult = new ArrayList<>(); // Not thread-safe
        numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .forEach(wrongResult::add); // This can cause issues
        System.out.println("Wrong approach size: " + wrongResult.size());
        
        // CORRECT: Using proper collectors
        List<Integer> correctResult = numbers.parallelStream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList()); // Thread-safe
        System.out.println("Correct approach: " + correctResult);
        
        // WRONG: Stateful operations
        List<Integer> statefulWrong = numbers.parallelStream()
            .map(n -> {
                // This is stateful and can cause issues in parallel
                return n * numbers.indexOf(n);
            })
            .collect(Collectors.toList());
        
        // CORRECT: Stateless operations
        List<Integer> statelessCorrect = numbers.parallelStream()
            .map(n -> n * n) // Stateless operation
            .collect(Collectors.toList());
        System.out.println("Stateless correct: " + statelessCorrect);
    }
}
```

---

## **8. Optional Class**

### **Basic Optional Usage**
```java
import java.util.*;

public class OptionalBasicExample {
    
    static class Person {
        private String name;
        private Optional<String> email;
        private Optional<Integer> age;
        
        public Person(String name, String email, Integer age) {
            this.name = name;
            this.email = Optional.ofNullable(email);
            this.age = Optional.ofNullable(age);
        }
        
        public String getName() { return name; }
        public Optional<String> getEmail() { return email; }
        public Optional<Integer> getAge() { return age; }
        
        @Override
        public String toString() {
            return String.format("Person{name='%s', email=%s, age=%s}", 
                               name, email.orElse("N/A"), age.map(String::valueOf).orElse("N/A"));
        }
    }
    
    public static void main(String[] args) {
        // Creating Optional instances
        Optional<String> empty = Optional.empty();
        Optional<String> nonEmpty = Optional.of("Hello");
        Optional<String> nullable = Optional.ofNullable(null);
        Optional<String> nullableWithValue = Optional.ofNullable("World");
        
        System.out.println("Empty: " + empty);
        System.out.println("Non-empty: " + nonEmpty);
        System.out.println("Nullable: " + nullable);
        System.out.println("Nullable with value: " + nullableWithValue);
        
        // Checking if value is present
        if (nonEmpty.isPresent()) {
            System.out.println("Value is present: " + nonEmpty.get());
        }
        
        if (empty.isEmpty()) { // Java 11+
            System.out.println("Empty optional is indeed empty");
        }
        
        // Using ifPresent()
        nonEmpty.ifPresent(value -> System.out.println("Processing: " + value));
        empty.ifPresent(value -> System.out.println("This won't print"));
        
        // Using ifPresentOrElse() - Java 9+
        nonEmpty.ifPresentOrElse(
            value -> System.out.println("Found: " + value),
            () -> System.out.println("Not found")
        );
        
        // Getting values with defaults
        String value1 = empty.orElse("Default Value");
        String value2 = nonEmpty.orElse("Default Value");
        
        System.out.println("Empty with default: " + value1);
        System.out.println("Non-empty with default: " + value2);
        
        // Using orElseGet() with Supplier
        String value3 = empty.orElseGet(() -> {
            System.out.println("Computing default value...");
            return "Computed Default";
        });
        System.out.println("Computed default: " + value3);
        
        // Using orElseThrow()
        try {
            String value4 = empty.orElseThrow(() -> new RuntimeException("Value not present"));
        } catch (RuntimeException e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
        
        // Working with Person objects
        List<Person> people = Arrays.asList(
            new Person("Alice", "alice@email.com", 25),
            new Person("Bob", null, 30),
            new Person("Charlie", "charlie@email.com", null),
            new Person("Diana", null, null)
        );
        
        System.out.println("\nPeople:");
        people.forEach(System.out::println);
        
        // Processing optional fields
        System.out.println("\nEmails:");
        people.forEach(person -> {
            person.getEmail().ifPresentOrElse(
                email -> System.out.println(person.getName() + ": " + email),
                () -> System.out.println(person.getName() + ": No email")
            );
        });
    }
}
```

### **Advanced Optional Operations**
```java
import java.util.*;
import java.util.stream.Collectors;

public class OptionalAdvancedExample {
    
    static class Address {
        private String street;
        private String city;
        private Optional<String> zipCode;
        
        public Address(String street, String city, String zipCode) {
            this.street = street;
            this.city = city;
            this.zipCode = Optional.ofNullable(zipCode);
        }
        
        public String getStreet() { return street; }
        public String getCity() { return city; }
        public Optional<String> getZipCode() { return zipCode; }
        
        @Override
        public String toString() {
            return String.format("Address{street='%s', city='%s', zipCode=%s}", 
                               street, city, zipCode.orElse("N/A"));
        }
    }
    
    static class Person {
        private String name;
        private Optional<Address> address;
        
        public Person(String name, Address address) {
            this.name = name;
            this.address = Optional.ofNullable(address);
        }
        
        public String getName() { return name; }
        public Optional<Address> getAddress() { return address; }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", new Address("123 Main St", "New York", "10001")),
            new Person("Bob", new Address("456 Oak Ave", "Boston", null)),
            new Person("Charlie", null)
        );
        
        // 1. map() - Transform the value if present
        List<Optional<String>> cities = people.stream()
            .map(person -> person.getAddress().map(Address::getCity))
            .collect(Collectors.toList());
        
        System.out.println("Cities (with Optional):");
        cities.forEach(cityOpt -> 
            System.out.println("  " + cityOpt.orElse("No city")));
        
        // 2. flatMap() - Avoid nested Optionals
        List<String> zipCodes = people.stream()
            .map(Person::getAddress)                    // Stream<Optional<Address>>
            .filter(Optional::isPresent)               // Keep only present addresses
            .map(Optional::get)                        // Stream<Address>
            .map(Address::getZipCode)                  // Stream<Optional<String>>
            .filter(Optional::isPresent)               // Keep only present zip codes
            .map(Optional::get)                        // Stream<String>
            .collect(Collectors.toList());
        
        System.out.println("Zip codes: " + zipCodes);
        
        // Better approach with flatMap
        List<String> zipCodesBetter = people.stream()
            .map(Person::getAddress)
            .flatMap(Optional::stream)                 // Java 9+ - converts Optional to Stream
            .map(Address::getZipCode)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
        
        System.out.println("Zip codes (better): " + zipCodesBetter);
        
        // 3. filter() - Filter based on predicate
        Optional<String> longZipCode = people.stream()
            .map(Person::getAddress)
            .flatMap(Optional::stream)
            .map(Address::getZipCode)
            .flatMap(Optional::stream)
            .filter(zip -> zip.length() > 4)
            .findFirst();
        
        System.out.println("Long zip code: " + longZipCode.orElse("None found"));
        
        // 4. Chaining Optional operations
        String result = Optional.of("  Hello World  ")
            .filter(s -> !s.trim().isEmpty())          // Keep if not empty after trim
            .map(String::trim)                         // Remove whitespace
            .map(String::toLowerCase)                  // Convert to lowercase
            .map(s -> s.replace(" ", "_"))             // Replace spaces with underscores
            .orElse("default");
        
        System.out.println("Chained result: " + result);
        
        // 5. Complex example - Safe navigation
        String personInfo = getPersonInfo(people.get(0));
        System.out.println("Person info: " + personInfo);
        
        String personInfoNull = getPersonInfo(people.get(2));
        System.out.println("Person info (null address): " + personInfoNull);
        
        // 6. or() method - Java 9+ (alternative Optional)
        Optional<String> primary = Optional.empty();
        Optional<String> secondary = Optional.of("Secondary value");
        
        String value = primary.or(() -> secondary).orElse("Default");
        System.out.println("Using or(): " + value);
        
        // 7. Working with collections of Optionals
        List<Optional<String>> optionalStrings = Arrays.asList(
            Optional.of("apple"),
            Optional.empty(),
            Optional.of("banana"),
            Optional.empty(),
            Optional.of("cherry")
        );
        
        List<String> presentValues = optionalStrings.stream()
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
        
        System.out.println("Present values: " + presentValues);
        
        // 8. Converting Optional to Stream (Java 9+)
        List<String> allCities = people.stream()
            .map(Person::getAddress)
            .flatMap(Optional::stream)
            .map(Address::getCity)
            .collect(Collectors.toList());
        
        System.out.println("All cities: " + allCities);
    }
    
    // Safe navigation example
    private static String getPersonInfo(Person person) {
        return Optional.ofNullable(person)
            .map(Person::getAddress)
            .flatMap(addressOpt -> addressOpt)
            .map(address -> address.getCity() + ", " + 
                          address.getZipCode().orElse("No ZIP"))
            .orElse("No address information");
    }
}
```

Optional Best Practices

import java.util.*;
import java.util.stream.Collectors;

public class OptionalBestPractices {
    
    static class UserService {
        private Map<String, String> users = Map.of(
            "alice", "Alice Smith",
            "bob", "Bob Jones"
        );
        
        // GOOD: Return Optional for methods that might not find a result
        public Optional<String> findUserById(String id) {
            return Optional.ofNullable(users.get(id));
        }
        
        // BAD: Don't use Optional for parameters
        public void updateUser(String id, Optional<String> name) {
            // This is not recommended
        }
        
        // GOOD: Use regular nullable parameter
        public void updateUserCorrect(String id, String name) {
            if (name != null) {
                users.put(id, name);
            }
        }
        
        // GOOD: Optional as return type for potentially null results
        public Optional<String> getFirstActiveUser() {
            return users.values().stream()
                .findFirst();
        }
        
        // BAD: Don't use Optional for collections
        public Optional<List<String>> getAllUsersBad() {
            List<String> userList = new ArrayList<>(users.values());
            return userList.isEmpty() ? Optional.empty() : Optional.of(userList);
        }
        
        // GOOD: Return empty collection instead
        public List<String> getAllUsersGood() {
            return new ArrayList<>(users.values());
        }
    }
    
    public static void main(String[] args) {
        UserService userService = new UserService();
        
        // GOOD: Proper Optional usage
       ```java
        // GOOD: Proper Optional usage
        Optional<String> user = userService.findUserById("alice");
        
        // ✅ GOOD: Use ifPresent() instead of isPresent() + get()
        user.ifPresent(name -> System.out.println("Found user: " + name));
        
        // ❌ BAD: Don't do this
        if (user.isPresent()) {
            System.out.println("Found user: " + user.get());
        }
        
        // ✅ GOOD: Use orElse() for simple defaults
        String userName = user.orElse("Unknown User");
        System.out.println("User name: " + userName);
        
        // ✅ GOOD: Use orElseGet() for expensive computations
        String expensiveDefault = user.orElseGet(() -> {
            // This is only called if Optional is empty
            return computeExpensiveDefault();
        });
        
        // ✅ GOOD: Use map() for transformations
        Optional<Integer> nameLength = user.map(String::length);
        nameLength.ifPresent(length -> 
            System.out.println("Name length: " + length));
        
        // ✅ GOOD: Use filter() for conditional logic
        Optional<String> longName = user.filter(name -> name.length() > 5);
        System.out.println("Long name: " + longName.orElse("Name is short"));
        
        // ✅ GOOD: Chain operations safely
        String result = userService.findUserById("bob")
            .filter(name -> name.startsWith("B"))
            .map(String::toUpperCase)
            .map(name -> "Mr. " + name)
            .orElse("User not found or doesn't start with B");
        
        System.out.println("Processed name: " + result);
        
        // ❌ BAD: Don't use Optional.get() without checking
        try {
            Optional<String> emptyUser = userService.findUserById("nonexistent");
            // String badResult = emptyUser.get(); // This will throw NoSuchElementException
        } catch (Exception e) {
            System.out.println("Avoided exception by not calling get()");
        }
        
        // ✅ GOOD: Use orElseThrow() with meaningful exceptions
        try {
            String requiredUser = userService.findUserById("charlie")
                .orElseThrow(() -> new IllegalArgumentException("User 'charlie' is required but not found"));
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }
        
        // ✅ GOOD: Working with collections and Optional
        List<String> userIds = Arrays.asList("alice", "bob", "charlie", "diana");
        
        List<String> foundUsers = userIds.stream()
            .map(userService::findUserById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        
        System.out.println("Found users: " + foundUsers);
        
        // ✅ BETTER: Using flatMap with Optional.stream() (Java 9+)
        List<String> foundUsersBetter = userIds.stream()
            .map(userService::findUserById)
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
        
        System.out.println("Found users (better): " + foundUsersBetter);
        
        // ✅ GOOD: Combining multiple Optionals
        Optional<String> firstName = Optional.of("John");
        Optional<String> lastName = Optional.of("Doe");
        
        Optional<String> fullName = firstName.flatMap(first ->
            lastName.map(last -> first + " " + last));
        
        System.out.println("Full name: " + fullName.orElse("Name not available"));
        
        // ✅ GOOD: Using Optional in business logic
        processOrder("ORDER123");
        processOrder("INVALID");
        
        // ❌ BAD: Don't use Optional for fields
        // class BadExample {
        //     private Optional<String> name; // Don't do this
        // }
        
        // ✅ GOOD: Use Optional only for return types
        class GoodExample {
            private String name; // Can be null
            
            public Optional<String> getName() {
                return Optional.ofNullable(name);
            }
        }
    }
    
    private static String computeExpensiveDefault() {
        System.out.println("Computing expensive default...");
        return "Expensive Default Value";
    }
    
    // Example of Optional in business logic
    private static void processOrder(String orderId) {
        findOrder(orderId)
            .filter(order -> order.getStatus().equals("PENDING"))
            .map(order -> {
                order.setStatus("PROCESSING");
                return order;
            })
            .ifPresentOrElse(
                order -> System.out.println("Processing order: " + order.getId()),
                () -> System.out.println("Order not found or not in PENDING status: " + orderId)
            );
    }
    
    private static Optional<Order> findOrder(String orderId) {
        if ("ORDER123".equals(orderId)) {
            return Optional.of(new Order(orderId, "PENDING"));
        }
        return Optional.empty();
    }
    
    static class Order {
        private String id;
        private String status;
        
        public Order(String id, String status) {
            this.id = id;
            this.status = status;
        }
        
        public String getId() { return id; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
```

---

## **9. Date and Time API (java.time)**

### **Basic Date and Time Operations**
```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

public class DateTimeBasicExample {
    public static void main(String[] args) {
        // Current date and time
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        Instant instant = Instant.now();
        
        System.out.println("Today: " + today);
        System.out.println("Current time: " + currentTime);
        System.out.println("Current date-time: " + currentDateTime);
        System.out.println("Zoned date-time: " + zonedDateTime);
        System.out.println("Instant: " + instant);
        
        // Creating specific dates and times
        LocalDate specificDate = LocalDate.of(2024, 12, 25);
        LocalDate specificDate2 = LocalDate.of(2024, Month.DECEMBER, 25);
        LocalTime specificTime = LocalTime.of(14, 30, 45);
        LocalDateTime specificDateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        System.out.println("\nSpecific date: " + specificDate);
        System.out.println("Specific time: " + specificTime);
        System.out.println("Specific date-time: " + specificDateTime);
        
        // Parsing from strings
        LocalDate parsedDate = LocalDate.parse("2024-12-25");
        LocalTime parsedTime = LocalTime.parse("14:30:45");
        LocalDateTime parsedDateTime = LocalDateTime.parse("2024-12-25T14:30:45");
        
        System.out.println("\nParsed date: " + parsedDate);
        System.out.println("Parsed time: " + parsedTime);
        System.out.println("Parsed date-time: " + parsedDateTime);
        
        // Date arithmetic
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonth = today.plusMonths(1);
        LocalDate nextYear = today.plusYears(1);
        
        System.out.println("\nTomorrow: " + tomorrow);
        System.out.println("Next week: " + nextWeek);
        System.out.println("Next month: " + nextMonth);
        System.out.println("Next year: " + nextYear);
        
        // Time arithmetic
        LocalTime laterTime = currentTime.plusHours(2).plusMinutes(30);
        LocalTime earlierTime = currentTime.minusHours(1).minusMinutes(15);
        
        System.out.println("\nLater time: " + laterTime);
        System.out.println("Earlier time: " + earlierTime);
        
        // Getting components
        int year = today.getYear();
        Month month = today.getMonth();
        int dayOfMonth = today.getDayOfMonth();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        
        System.out.println("\nYear: " + year);
        System.out.println("Month: " + month);
        System.out.println("Day of month: " + dayOfMonth);
        System.out.println("Day of week: " + dayOfWeek);
        
        // Comparisons
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 12, 31);
        
        System.out.println("\nIs date1 before date2? " + date1.isBefore(date2));
        System.out.println("Is date1 after date2? " + date1.isAfter(date2));
        System.out.println("Are dates equal? " + date1.isEqual(date2));
        
        // Period and Duration
        Period period = Period.between(date1, date2);
        System.out.println("Period between dates: " + period);
        System.out.println("Days between: " + period.getDays());
        System.out.println("Months between: " + period.getMonths());
        
        Duration duration = Duration.between(
            LocalTime.of(9, 0), 
            LocalTime.of(17, 30)
        );
        System.out.println("Duration: " + duration);
        System.out.println("Hours: " + duration.toHours());
        System.out.println("Minutes: " + duration.toMinutes());
    }
}
```

### **Advanced Date and Time Operations**
```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.*;
import java.util.Locale;

public class DateTimeAdvancedExample {
    public static void main(String[] args) {
        // Working with time zones
        ZoneId newYorkZone = ZoneId.of("America/New_York");
        ZoneId tokyoZone = ZoneId.of("Asia/Tokyo");
        ZoneId utcZone = ZoneId.of("UTC");
        
        ZonedDateTime nyTime = ZonedDateTime.now(newYorkZone);
        ZonedDateTime tokyoTime = nyTime.withZoneSameInstant(tokyoZone);
        ZonedDateTime utcTime = nyTime.withZoneSameInstant(utcZone);
        
        System.out.println("New York time: " + nyTime);
        System.out.println("Tokyo time: " + tokyoTime);
        System.out.println("UTC time: " + utcTime);
        
        // Formatting dates and times
        LocalDateTime dateTime = LocalDateTime.now();
        
        // Predefined formatters
        System.out.println("\nFormatted dates:");
        System.out.println("ISO_LOCAL_DATE: " + dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println("ISO_LOCAL_TIME: " + dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        System.out.println("ISO_LOCAL_DATE_TIME: " + dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Localized formatters
        System.out.println("SHORT: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
        System.out.println("MEDIUM: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        System.out.println("LONG: " + dateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)));
        
        // Custom formatters
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DateTimeFormatter customFormatter2 = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' hh:mm a");
        
        System.out.println("Custom 1: " + dateTime.format(customFormatter));
        System.out.println("Custom 2: " + dateTime.format(customFormatter2));
        
        // Parsing with custom formatters
        String dateString = "25/12/2024 14:30:45";
        LocalDateTime parsedDateTime = LocalDateTime.parse(dateString, customFormatter);
        System.out.println("Parsed: " + parsedDateTime);
        
        // Temporal adjusters
        LocalDate today = LocalDate.now();
        
        System.out.println("\nTemporal adjusters:");
        System.out.println("First day of month: " + today.with(TemporalAdjusters.firstDayOfMonth()));
        System.out.println("Last day of month: " + today.with(TemporalAdjusters.lastDayOfMonth()));
        System.out.println("First day of next month: " + today.with(TemporalAdjusters.firstDayOfNextMonth()));
        System.out.println("First day of year: " + today.with(TemporalAdjusters.firstDayOfYear()));
        System.out.println("Last day of year: " + today.with(TemporalAdjusters.lastDayOfYear()));
        
        // Day of week adjusters
        System.out.println("Next Monday: " + today.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        System.out.println("Previous Friday: " + today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY)));
        System.out.println("Next or same Saturday: " + today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)));
        
        // Custom temporal adjuster
        TemporalAdjuster nextWorkingDay = temporal -> {
            DayOfWeek dayOfWeek = DayOfWeek.from(temporal);
            int daysToAdd = switch (dayOfWeek) {
                case FRIDAY -> 3; // Friday -> Monday
                case SATURDAY -> 2; // Saturday -> Monday
                default -> 1; // Any other day -> next day
            };
            return temporal.plus(daysToAdd, ChronoUnit.DAYS);
        };
        
        System.out.println("Next working day: " + today.with(nextWorkingDay));
        
        // Working with different calendar systems
        System.out.println("\nDifferent calendar systems:");
        
        // Japanese calendar
        JapaneseDate japaneseDate = JapaneseDate.now();
        System.out.println("Japanese date: " + japaneseDate);
        
        // Hijrah calendar
        HijrahDate hijrahDate = HijrahDate.now();
        System.out.println("Hijrah date: " + hijrahDate);
        
        // Converting between calendar systems
        LocalDate localDate = LocalDate.from(japaneseDate);
        System.out.println("Converted to LocalDate: " + localDate);
        
        // Working with Instant and timestamps
        ```java
        // Working with Instant and timestamps
        Instant now = Instant.now();
        Instant futureInstant = now.plus(Duration.ofHours(24));
        Instant pastInstant = now.minus(Duration.ofDays(7));
        
        System.out.println("\nInstant operations:");
        System.out.println("Now: " + now);
        System.out.println("24 hours later: " + futureInstant);
        System.out.println("7 days ago: " + pastInstant);
        
        // Converting between Instant and LocalDateTime
        LocalDateTime localFromInstant = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        Instant instantFromLocal = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant();
        
        System.out.println("Local from instant: " + localFromInstant);
        System.out.println("Instant from local: " + instantFromLocal);
        
        // Epoch time
        long epochSeconds = now.getEpochSecond();
        long epochMillis = now.toEpochMilli();
        Instant fromEpoch = Instant.ofEpochSecond(epochSeconds);
        
        System.out.println("Epoch seconds: " + epochSeconds);
        System.out.println("Epoch millis: " + epochMillis);
        System.out.println("From epoch: " + fromEpoch);
        
        // Clock for testing
        Clock systemClock = Clock.systemDefaultZone();
        Clock fixedClock = Clock.fixed(Instant.parse("2024-12-25T10:15:30Z"), ZoneId.of("UTC"));
        
        System.out.println("\nClock operations:");
        System.out.println("System clock time: " + LocalDateTime.now(systemClock));
        System.out.println("Fixed clock time: " + LocalDateTime.now(fixedClock));
        
        // Measuring time intervals
        measurePerformance();
        
        // Working with different locales
        workWithLocales();
    }
    
    private static void measurePerformance() {
        System.out.println("\nPerformance measurement:");
        
        Instant start = Instant.now();
        
        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        System.out.println("Operation took: " + duration.toMillis() + " milliseconds");
        System.out.println("Operation took: " + duration.toNanos() + " nanoseconds");
    }
    
    private static void workWithLocales() {
        System.out.println("\nLocalized formatting:");
        
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        // Different locales
        Locale[] locales = {
            Locale.US,
            Locale.GERMANY,
            Locale.FRANCE,
            Locale.JAPAN,
            new Locale("ar", "SA") // Arabic - Saudi Arabia
        };
        
        for (Locale locale : locales) {
            DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.FULL)
                .withLocale(locale);
            
            System.out.println(locale.getDisplayName() + ": " + dateTime.format(formatter));
        }
    }
}
```

### **Date and Time Utilities and Best Practices**
```java
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Stream;

public class DateTimeUtilitiesExample {
    
    // Utility class for common date operations
    public static class DateTimeUtils {
        
        // Check if a year is a leap year
        public static boolean isLeapYear(int year) {
            return Year.of(year).isLeap();
        }
        
        // Get the number of days in a month
        public static int getDaysInMonth(int year, int month) {
            return YearMonth.of(year, month).lengthOfMonth();
        }
        
        // Get all dates between two dates
        public static Stream<LocalDate> getDatesBetween(LocalDate start, LocalDate end) {
            return start.datesUntil(end.plusDays(1));
        }
        
        // Get business days between two dates (excluding weekends)
        public static long getBusinessDaysBetween(LocalDate start, LocalDate end) {
            return start.datesUntil(end.plusDays(1))
                .filter(date -> !isWeekend(date))
                .count();
        }
        
        // Check if a date is a weekend
        public static boolean isWeekend(LocalDate date) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        }
        
        // Get the next business day
        public static LocalDate getNextBusinessDay(LocalDate date) {
            LocalDate nextDay = date.plusDays(1);
            while (isWeekend(nextDay)) {
                nextDay = nextDay.plusDays(1);
            }
            return nextDay;
        }
        
        // Get the last business day of the month
        public static LocalDate getLastBusinessDayOfMonth(LocalDate date) {
            LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
            while (isWeekend(lastDay)) {
                lastDay = lastDay.minusDays(1);
            }
            return lastDay;
        }
        
        // Safe date parsing with multiple formats
        public static LocalDate parseDate(String dateString) {
            List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")
            );
            
            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDate.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    // Try next formatter
                }
            }
            throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
        }
        
        // Calculate age
        public static int calculateAge(LocalDate birthDate, LocalDate currentDate) {
            return Period.between(birthDate, currentDate).getYears();
        }
        
        // Get quarter of the year
        public static int getQuarter(LocalDate date) {
            return (date.getMonthValue() - 1) / 3 + 1;
        }
        
        // Convert time zone safely
        public static ZonedDateTime convertTimeZone(ZonedDateTime dateTime, ZoneId targetZone) {
            return dateTime.withZoneSameInstant(targetZone);
        }
        
        // Format duration in human-readable format
        public static String formatDuration(Duration duration) {
            long days = duration.toDays();
            long hours = duration.toHoursPart();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();
            
            StringBuilder sb = new StringBuilder();
            if (days > 0) sb.append(days).append(" days ");
            if (hours > 0) sb.append(hours).append(" hours ");
            if (minutes > 0) sb.append(minutes).append(" minutes ");
            if (seconds > 0) sb.append(seconds).append(" seconds");
            
            return sb.toString().trim();
        }
    }
    
    public static void main(String[] args) {
        // Test utility methods
        System.out.println("=== Date Time Utilities Demo ===");
        
        // Leap year check
        System.out.println("Is 2024 a leap year? " + DateTimeUtils.isLeapYear(2024));
        System.out.println("Is 2023 a leap year? " + DateTimeUtils.isLeapYear(2023));
        
        // Days in month
        System.out.println("Days in February 2024: " + DateTimeUtils.getDaysInMonth(2024, 2));
        System.out.println("Days in February 2023: " + DateTimeUtils.getDaysInMonth(2023, 2));
        
        // Date ranges
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 10);
        
        System.out.println("\nDates between " + start + " and " + end + ":");
        DateTimeUtils.getDatesBetween(start, end)
            .forEach(date -> System.out.println("  " + date + " (" + date.getDayOfWeek() + ")"));
        
        // Business days
        long businessDays = DateTimeUtils.getBusinessDaysBetween(start, end);
        System.out.println("Business days: " + businessDays);
        
        // Weekend check
        LocalDate saturday = LocalDate.of(2024, 1, 6);
        LocalDate monday = LocalDate.of(2024, 1, 8);
        System.out.println("Is " + saturday + " a weekend? " + DateTimeUtils.isWeekend(saturday));
        System.out.println("Is " + monday + " a weekend? " + DateTimeUtils.isWeekend(monday));
        
        // Next business day
        LocalDate friday = LocalDate.of(2024, 1, 5);
        System.out.println("Next business day after " + friday + ": " + 
                          DateTimeUtils.getNextBusinessDay(friday));
        
        // Last business day of month
        LocalDate anyDayInMonth = LocalDate.of(2024, 1, 15);
        System.out.println("Last business day of January 2024: " + 
                          DateTimeUtils.getLastBusinessDayOfMonth(anyDayInMonth));
        
        // Safe date parsing
        String[] dateStrings = {
            "2024-01-15",
            "15/01/2024",
            "01/15/2024",
            "15-01-2024",
            "2024/01/15"
        };
        
        System.out.println("\nParsing different date formats:");
        for (String dateString : dateStrings) {
            try {
                LocalDate parsed = DateTimeUtils.parseDate(dateString);
                System.out.println(dateString + " -> " + parsed);
            } catch (DateTimeParseException e) {
                System.out.println(dateString + " -> Failed to parse");
            }
        }
        
        // Age calculation
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDate today = LocalDate.now();
        int age = DateTimeUtils.calculateAge(birthDate, today);
        System.out.println("\nAge calculation:");
        System.out.println("Birth date: " + birthDate);
        System.out.println("Current date: " + today);
        System.out.println("Age: " + age + " years");
        
        // Quarter calculation
        LocalDate[] testDates = {
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 4, 15),
            LocalDate.of(2024, 7, 15),
            LocalDate.of(2024, 10, 15)
        };
        
        System.out.println("\nQuarter calculation:");
        for (LocalDate date : testDates) {
            System.out.println(date + " is in Q" + DateTimeUtils.getQuarter(date));
        }
        
        // Time zone conversion
        ZonedDateTime nyTime = ZonedDateTime.of(2024, 1, 15, 14, 30, 0, 0, 
                                               ZoneId.of("America/New_York"));
        ZonedDateTime tokyoTime = DateTimeUtils.convertTimeZone(nyTime, ZoneId.of("Asia/Tokyo"));
        
        System.out.println("\nTime zone conversion:");
        System.out.println("New York: " + nyTime);
        System.out.println("Tokyo: " + tokyoTime);
        
        // Duration formatting
        Duration[] durations = {
            Duration.ofSeconds(45),
            Duration.ofMinutes(30),
            Duration.ofHours(2).plusMinutes(15),
            Duration.ofDays(3).plusHours(4).plusMinutes(30).plusSeconds(15)
        };
        
        System.out.println("\nDuration formatting:");
        for (Duration duration : durations) {
            System.out.println(duration + " -> " + DateTimeUtils.formatDuration(duration));
        }
        
        // Working with periods
        demonstratePeriods();
        
        // Common date calculations
        demonstrateCommonCalculations();
    }
    
    private static void demonstratePeriods() {
        System.out.println("\n=== Period Operations ===");
        
        LocalDate startDate = LocalDate.of(2020, 1, 15);
        LocalDate endDate = LocalDate.of(2024, 3, 20);
        
        Period period = Period.between(startDate, endDate);
        
        System.out.println("Start date: " + startDate);
        System.out.println("End date: " + endDate);
        System.out.println("Period: " + period);
        System.out.println("Years: " + period.getYears());
        System.out.println("Months: " + period.getMonths());
        System.out.println("Days: " + period.getDays());
        
        // Total days using ChronoUnit
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        long totalMonths = ChronoUnit.MONTHS.between(startDate, endDate);
        long totalYears = ChronoUnit.YEARS.between(startDate, endDate);
        
        System.out.println("Total days: " + totalDays);
        System.out.println("Total months: " + totalMonths);
        System.out.println("Total years: " + totalYears);
    }
    
    ```java
    private static void demonstrateCommonCalculations() {
        System.out.println("\n=== Common Date Calculations ===");
        
        LocalDate today = LocalDate.now();
        
        // Start and end of current week
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        System.out.println("Today: " + today);
        System.out.println("Start of week: " + startOfWeek);
        System.out.println("End of week: " + endOfWeek);
        
        // Start and end of current month
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        
        System.out.println("Start of month: " + startOfMonth);
        System.out.println("End of month: " + endOfMonth);
        
        // Start and end of current year
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        
        System.out.println("Start of year: " + startOfYear);
        System.out.println("End of year: " + endOfYear);
        
        // Start and end of current quarter
        int currentQuarter = DateTimeUtils.getQuarter(today);
        LocalDate startOfQuarter = today.withMonth((currentQuarter - 1) * 3 + 1).withDayOfMonth(1);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);
        
        System.out.println("Current quarter: Q" + currentQuarter);
        System.out.println("Start of quarter: " + startOfQuarter);
        System.out.println("End of quarter: " + endOfQuarter);
        
        // Days until specific events
        LocalDate christmas = LocalDate.of(today.getYear(), 12, 25);
        if (christmas.isBefore(today)) {
            christmas = christmas.plusYears(1); // Next year's Christmas
        }
        long daysUntilChristmas = ChronoUnit.DAYS.between(today, christmas);
        
        LocalDate newYear = LocalDate.of(today.getYear() + 1, 1, 1);
        long daysUntilNewYear = ChronoUnit.DAYS.between(today, newYear);
        
        System.out.println("Days until Christmas: " + daysUntilChristmas);
        System.out.println("Days until New Year: " + daysUntilNewYear);
        
        // Age-related calculations
        LocalDate birthDate = LocalDate.of(1990, 6, 15);
        Period ageAsPeriod = Period.between(birthDate, today);
        long ageInDays = ChronoUnit.DAYS.between(birthDate, today);
        long ageInWeeks = ChronoUnit.WEEKS.between(birthDate, today);
        
        System.out.println("\nAge calculations for birth date " + birthDate + ":");
        System.out.println("Age: " + ageAsPeriod.getYears() + " years, " + 
                          ageAsPeriod.getMonths() + " months, " + 
                          ageAsPeriod.getDays() + " days");
        System.out.println("Age in days: " + ageInDays);
        System.out.println("Age in weeks: " + ageInWeeks);
        
        // Next occurrence of specific day
        LocalDate nextFriday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        
        System.out.println("Next Friday: " + nextFriday);
        System.out.println("Next Monday (or today if Monday): " + nextMonday);
        
        // Working days calculations
        LocalDate monthStart = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = today.with(TemporalAdjusters.lastDayOfMonth());
        long workingDaysInMonth = DateTimeUtils.getBusinessDaysBetween(monthStart, monthEnd);
        long workingDaysElapsed = DateTimeUtils.getBusinessDaysBetween(monthStart, today);
        
        System.out.println("Working days in current month: " + workingDaysInMonth);
        System.out.println("Working days elapsed this month: " + workingDaysElapsed);
        
        // Time until end of day, week, month, year
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
        LocalDateTime endOfWeekTime = endOfWeek.atTime(23, 59, 59);
        LocalDateTime endOfMonthTime = endOfMonth.atTime(23, 59, 59);
        LocalDateTime endOfYearTime = endOfYear.atTime(23, 59, 59);
        
        Duration timeUntilEndOfDay = Duration.between(now, endOfDay);
        Duration timeUntilEndOfWeek = Duration.between(now, endOfWeekTime);
        Duration timeUntilEndOfMonth = Duration.between(now, endOfMonthTime);
        Duration timeUntilEndOfYear = Duration.between(now, endOfYearTime);
        
        System.out.println("\nTime remaining:");
        System.out.println("Until end of day: " + DateTimeUtils.formatDuration(timeUntilEndOfDay));
        System.out.println("Until end of week: " + DateTimeUtils.formatDuration(timeUntilEndOfWeek));
        System.out.println("Until end of month: " + DateTimeUtils.formatDuration(timeUntilEndOfMonth));
        System.out.println("Until end of year: " + DateTimeUtils.formatDuration(timeUntilEndOfYear));
        
        // Seasonal calculations
        demonstrateSeasonalCalculations(today);
    }
    
    private static void demonstrateSeasonalCalculations(LocalDate date) {
        System.out.println("\n=== Seasonal Calculations ===");
        
        // Define seasons (Northern Hemisphere)
        LocalDate springStart = LocalDate.of(date.getYear(), 3, 20);
        LocalDate summerStart = LocalDate.of(date.getYear(), 6, 21);
        LocalDate autumnStart = LocalDate.of(date.getYear(), 9, 22);
        LocalDate winterStart = LocalDate.of(date.getYear(), 12, 21);
        
        String currentSeason;
        LocalDate nextSeasonStart;
        
        if (date.isBefore(springStart)) {
            currentSeason = "Winter";
            nextSeasonStart = springStart;
        } else if (date.isBefore(summerStart)) {
            currentSeason = "Spring";
            nextSeasonStart = summerStart;
        } else if (date.isBefore(autumnStart)) {
            currentSeason = "Summer";
            nextSeasonStart = autumnStart;
        } else if (date.isBefore(winterStart)) {
            currentSeason = "Autumn";
            nextSeasonStart = winterStart;
        } else {
            currentSeason = "Winter";
            nextSeasonStart = LocalDate.of(date.getYear() + 1, 3, 20);
        }
        
        long daysUntilNextSeason = ChronoUnit.DAYS.between(date, nextSeasonStart);
        
        System.out.println("Current season: " + currentSeason);
        System.out.println("Days until next season: " + daysUntilNextSeason);
        
        // Daylight saving time transitions (example for US)
        // Note: This is a simplified example - actual DST rules are more complex
        LocalDate dstStart = getSecondSundayOfMarch(date.getYear());
        LocalDate dstEnd = getFirstSundayOfNovember(date.getYear());
        
        boolean isDST = !date.isBefore(dstStart) && date.isBefore(dstEnd);
        System.out.println("Is Daylight Saving Time (US): " + isDST);
        
        if (isDST) {
            long daysUntilDSTEnd = ChronoUnit.DAYS.between(date, dstEnd);
            System.out.println("Days until DST ends: " + daysUntilDSTEnd);
        } else {
            LocalDate nextDSTStart = date.isBefore(dstStart) ? dstStart : 
                                   getSecondSundayOfMarch(date.getYear() + 1);
            long daysUntilDSTStart = ChronoUnit.DAYS.between(date, nextDSTStart);
            System.out.println("Days until DST starts: " + daysUntilDSTStart);
        }
    }
    
    private static LocalDate getSecondSundayOfMarch(int year) {
        LocalDate firstOfMarch = LocalDate.of(year, 3, 1);
        LocalDate firstSunday = firstOfMarch.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return firstSunday.plusWeeks(1);
    }
    
    private static LocalDate getFirstSundayOfNovember(int year) {
        LocalDate firstOfNovember = LocalDate.of(year, 11, 1);
        return firstOfNovember.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
}
```

---

## **10. Collections Framework Deep Dive**

### **Advanced List Operations**
```java
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AdvancedListOperations {
    
    public static void main(String[] args) {
        // Different List implementations and their characteristics
        demonstrateListImplementations();
        
        // Advanced list operations
        demonstrateAdvancedOperations();
        
        // List algorithms and utilities
        demonstrateListAlgorithms();
        
        // Thread-safe list operations
        demonstrateThreadSafeLists();
        
        // Custom list implementations
        demonstrateCustomList();
    }
    
    private static void demonstrateListImplementations() {
        System.out.println("=== List Implementations Comparison ===");
        
        // ArrayList - best for random access, iteration
        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        
        // LinkedList - best for frequent insertions/deletions
        List<String> linkedList = new LinkedList<>();
        linkedList.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        
        // Vector - synchronized, legacy
        List<String> vector = new Vector<>();
        vector.addAll(Arrays.asList("A", "B", "C", "D", "E"));
        
        System.out.println("ArrayList: " + arrayList);
        System.out.println("LinkedList: " + linkedList);
        System.out.println("Vector: " + vector);
        
        // Performance comparison for different operations
        performanceComparison();
    }
    
    private static void performanceComparison() {
        System.out.println("\n--- Performance Comparison ---");
        
        int size = 100000;
        
        // ArrayList vs LinkedList for random access
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        
        // Fill lists
        for (int i = 0; i < size; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }
        
        // Random access performance
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int index = (int) (Math.random() * size);
            arrayList.get(index);
        }
        long arrayListTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            int index = (int) (Math.random() * size);
            linkedList.get(index);
        }
        long linkedListTime = System.nanoTime() - start;
        
        System.out.println("Random access (1000 operations):");
        System.out.println("ArrayList: " + arrayListTime / 1_000_000.0 + " ms");
        System.out.println("LinkedList: " + linkedListTime / 1_000_000.0 + " ms");
        
        // Insertion at beginning performance
        List<Integer> arrayListInsert = new ArrayList<>();
        List<Integer> linkedListInsert = new LinkedList<>();
        
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            arrayListInsert.add(0, i);
        }
        long arrayListInsertTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            linkedListInsert.add(0, i);
        }
        long linkedListInsertTime = System.nanoTime() - start;
        
        System.out.println("\nInsertion at beginning (10000 operations):");
        System.out.println("ArrayList: " + arrayListInsertTime / 1_000_000.0 + " ms");
        System.out.println("LinkedList: " + linkedListInsertTime / 1_000_000.0 + " ms");
    }
    


 
