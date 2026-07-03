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


 
