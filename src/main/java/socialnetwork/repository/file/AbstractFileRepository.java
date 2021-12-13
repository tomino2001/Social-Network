package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * loads the data from file to memory
     */
    private void loadData() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] args = line.split(";");
                E entity = extractEntity(Arrays.asList(args));
                super.save(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * extract entity  - template method design pattern
     * creates an entity of type E having a specified list of @code attributes
     *
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);


    protected abstract String createEntityAsString(E entity);

    /**
     * @param entity entity must be not null
     * @return entity
     */
    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile(entity);
        }
        return result;
    }

    /**
     * deletes the entity with the given id
     *
     * @param id id must be not null
     */
    @Override
    public void delete(ID id) {
        super.delete(id);
        rewriteToFile();
    }

    /**
     * updates an entity
     *
     * @param entity entity must not be null
     */
    @Override
    public void update(E entity) {
        super.update(entity);
        rewriteToFile();
    }


    /**
     * rewrites the file
     */
    protected void rewriteToFile() {
        FileOutputStream fooStream = null;
        try {
            fooStream = new FileOutputStream(this.fileName, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Iterable<E> entities = super.findAll();
        for (E e : entities) {
            String line = createEntityAsString(e) + "\n";
            try {
                if (fooStream != null) {
                    fooStream.write(line.getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
            if (fooStream != null) {
                fooStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes an entity in file
     *
     * @param entity
     */
    protected void writeToFile(E entity) {
        String entityAsString = createEntityAsString(entity);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName, true))) {
            bufferedWriter.write(entityAsString);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

