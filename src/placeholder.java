// == CS400 Spring 2024 File Header Information ==
// Name: Remington Reichmann
// Email: rreichmann@wisc.edu
// Lecturer: Gary Dahl
// Notes to Grader: NONE

import java.util.LinkedList;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

public class HashtableMap<KeyType, ValueType> implements MapADT<KeyType, ValueType>{
  protected class Pair {
    
    public KeyType key;
    public ValueType value;

    public Pair(KeyType key, ValueType value) {
        this.key = key;
        this.value = value;
    }

  }
  
  /**
   * Stores the maximum amount of pairs we are allowed to have in the map
   */
  private int capacity;
  
  /**
   * The current number of pairs in the map
   */
  private int size;
  
  /**
   * Represents the map. Contains linked lists of pairs.
   */
  private LinkedList<Pair>[] table;
  
  /**
   * Initializes a new HashtableMap with a specified capacity
   * @param capacity the capacity of the map 
   */
  @SuppressWarnings("unchecked")
  public HashtableMap(int capacity) {
    this.capacity = capacity;
    table = new LinkedList[capacity];
  }
  
  /**
   * Initializes a new HashtableMap with a specified capacity of 64
   */
  @SuppressWarnings("unchecked")
  public HashtableMap() {
    this.capacity = 64;
    table = new LinkedList[capacity];
  }
  
  /**
   * Helper method to reorganize everything in the table. This method already handles calculating
   * the load capacity, so this method should be called anytime something new is put into the map.
  */
  @SuppressWarnings("unchecked")
  private void rehash() {
    // Double capacity when needed
    if(getSize() / (double)capacity >= 0.8 ) {
      capacity *= 2;
      
      LinkedList<Pair>[] newTable = new LinkedList[capacity];
      
      // Loop through every pair currently in the table
      for(int i = 0; i < table.length; i++) {
        if(table[i] != null) {
          for(int j = 0; j < table[i].size(); j++) {
            // Calculate the new index for the pair
            int index = table[i].get(j).hashCode() % capacity;
            // If a linked list doesn't currently exist at this index in the table, initialize one.
            if(newTable[index] == null) {
              newTable[index] = new LinkedList<>();
            }
            newTable[index].add(table[i].get(j));
          }
        }
      }
      table = newTable;
    }
  }
  
  /**
   * Adds a new pair into the map with a specified key and value.
   * 
   * @param key the key for the pair we are trying to insert
   * @param value the actual value of the pair
   * @throws NullPointerException if key is null
   * @throws IllegalArgumentException if a pair with the same key as the parameter is already in
   * the map
   */
  public void put(KeyType key, ValueType value) throws IllegalArgumentException{
    if(key == null) {
      throw new NullPointerException("Key is null.");
    }
    
    if(this.containsKey(key)) {
      throw new IllegalArgumentException(key + " is already in the map.");
    }
 
    // Calculate index for the pair
    int index = key.hashCode() % capacity;
    // If a linked list doesn't currently exist at this index, in the table, initialize one.
    if(table[index] == null) {
      table[index] = new LinkedList<>();
    }
    table[index].add(new Pair(key, value));
    size++;
    
    // Rehash calculates whether or not the table needs to be rehashed, so the method should be 
    // called after every insert.
    rehash();
  }
  
  /**
   * Checks to see if the specified key is in the map.
   * 
