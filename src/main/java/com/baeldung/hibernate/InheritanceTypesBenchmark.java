package com.baeldung.hibernate;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import com.baeldung.hibernate.pojo.inheritance.Book;
import com.baeldung.hibernate.pojo.inheritance.Car;
import com.baeldung.hibernate.pojo.inheritance.Employee;
import com.baeldung.hibernate.pojo.inheritance.Item;
import com.baeldung.hibernate.pojo.inheritance.Pet;

@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 15)
@Fork(value = 1)
public class InheritanceTypesBenchmark
{
	static EntityManagerFactory mariaDbFactory = Persistence.createEntityManagerFactory("com.baeldung.inheritance.mariadb");
	static EntityManagerFactory postgresFactory = Persistence.createEntityManagerFactory("com.baeldung.inheritance.postgres");
	
	static Random random = new Random();
	
	private static String getRandomString(int length)
	{
		String longString = random.ints(32, 127).limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return longString;
	}
	
	public static void main(String[] argv)
	{
		InheritanceTypesBenchmark benchmark = new InheritanceTypesBenchmark();
		Map<String, String> map = new HashMap<>();
		// map.put("hibernate.show_sql", "true");
		mariaDbFactory = Persistence.createEntityManagerFactory("com.baeldung.inheritance.mariadb", map);
		
		/* save */
		SaveJoinedMariaDbState saveJoinedMariaDbState = new SaveJoinedMariaDbState();
		saveJoinedMariaDbState.setupTrial();
		saveJoinedMariaDbState.setupEach();
		benchmark.executeSaveJoinedMariaDb(saveJoinedMariaDbState);
		// saveJoinedMariaDbState.tearDownTrial();
		
		SaveMappedSuperclassMariaDbState saveMappedSuperclassMariaDbState = new SaveMappedSuperclassMariaDbState();
		saveMappedSuperclassMariaDbState.setupTrial();
		saveMappedSuperclassMariaDbState.setupEach();
		benchmark.executeSaveMappedSuperclassMariaDb(saveMappedSuperclassMariaDbState);
		// saveMappedSuperclassMariaDbState.tearDownTrial();
		
		SaveSingleTableMariaDbState saveSingleTableMariaDbState = new SaveSingleTableMariaDbState();
		saveSingleTableMariaDbState.setupTrial();
		saveSingleTableMariaDbState.setupEach();
		benchmark.executeSaveSingleTableMariaDb(saveSingleTableMariaDbState);
		// saveSingleTableMariaDbState.tearDownTrial();
		
		SaveTablePerClassMariaDbState saveTablePerClassMariaDbState = new SaveTablePerClassMariaDbState();
		saveTablePerClassMariaDbState.setupTrial();
		saveTablePerClassMariaDbState.setupEach();
		benchmark.executeSaveTablePerClassMariaDb(saveTablePerClassMariaDbState);
		// saveTablePerClassMariaDbState.tearDownTrial();
		
		/* select */
		SelectJoinedMariaDbState selectJoinedMariaDbState = new SelectJoinedMariaDbState();
		selectJoinedMariaDbState.setupTrial();
		selectJoinedMariaDbState.setupEach();
		benchmark.executeSelectJoinedMariaDb(selectJoinedMariaDbState);
		// selectJoinedMariaDbState.tearDownTrial();
		
		SelectMappedSuperclassMariaDbState selectMappedSuperclassMariaDbState = new SelectMappedSuperclassMariaDbState();
		selectMappedSuperclassMariaDbState.setupTrial();
		selectMappedSuperclassMariaDbState.setupEach();
		benchmark.executeSelectMappedSuperclassMariaDb(selectMappedSuperclassMariaDbState);
		// selectMappedSuperclassMariaDbState.tearDownTrial();
		
		SelectSingleTableMariaDbState selectSingleTableMariaDbState = new SelectSingleTableMariaDbState();
		selectSingleTableMariaDbState.setupTrial();
		selectSingleTableMariaDbState.setupEach();
		benchmark.executeSelectSingleTableMariaDb(selectSingleTableMariaDbState);
		// selectSingleTableMariaDbState.tearDownTrial();
		
		SelectTablePerClassMariaDbState selectTablePerClassMariaDbState = new SelectTablePerClassMariaDbState();
		selectTablePerClassMariaDbState.setupTrial();
		selectTablePerClassMariaDbState.setupEach();
		benchmark.executeSelectTablePerClassMariaDb(selectTablePerClassMariaDbState);
		// selectTablePerClassMariaDbState.tearDownTrial();
	}
	
	public static abstract class AbstractState
	{
		static int INITIAL_ROWS_COUNT = 100_000;
		
		protected EntityManagerFactory factory;
		
		protected Class<? extends Item> klass;
		public Item object;
		
		public AbstractState(EntityManagerFactory factory, Class<? extends Item> klass)
		{
			this.factory = factory;
			this.klass = klass;
		}
		
		@Setup(Level.Trial)
		public void setupTrial()
		{
			EntityManager em = factory.createEntityManager();
			Number count = ((Number) em.createQuery("SELECT COUNT(o) FROM " + klass.getCanonicalName() + " o").getSingleResult());
			if (count.intValue() < INITIAL_ROWS_COUNT)
			{
				em.getTransaction().begin();
				for (int i = count.intValue(); i < INITIAL_ROWS_COUNT; i++)
				{
					createAndFill();
					em.persist(object);
					if (i % 1000 == 0) {
						em.flush();
						em.clear();
					}
				}
				em.getTransaction().commit();
			}
			em.close();
		}
		
		protected void createAndFill()
		{
			try
			{
				Item object = klass.getConstructor().newInstance();
				object.setId(random.nextLong());
				final int length = 128;
				object.setField1(getRandomString(length));
				object.setField2(getRandomString(length));
				object.setField3(getRandomString(length));
				object.setField4(getRandomString(length));
				object.setField5(getRandomString(length));
				object.setField6(getRandomString(length));
				object.setField7(getRandomString(length));
				object.setField8(getRandomString(length));
				this.object = object;
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static abstract class AbstractSaveState extends AbstractState
	{
		public EntityManager em;
		
		public AbstractSaveState(EntityManagerFactory factory, Class<? extends Item> klass)
		{
			super(factory, klass);
		}
		
		@Setup(Level.Iteration)
		public void setupEachIter()
		{
			em = factory.createEntityManager();
			em.getTransaction().begin();
		}
		
		@TearDown(Level.Iteration)
		public void tearDownEachIter()
		{
			em.getTransaction().commit();
			em.close();
		}
		
		@Setup(Level.Invocation)
		public void setupEach()
		{
			createAndFill();
		}
		
		@TearDown(Level.Invocation)
		public void tearDownEach()
		{
			em.clear();
		}
	}
	
	@State(Scope.Thread)
	public static class SaveTablePerClassMariaDbState extends AbstractSaveState
	{
		public SaveTablePerClassMariaDbState()
		{
			super(mariaDbFactory, Car.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SaveTablePerClassPostgresState extends AbstractSaveState
	{
		public SaveTablePerClassPostgresState()
		{
			super(postgresFactory, Car.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveTablePerClassMariaDb(SaveTablePerClassMariaDbState state)
	{
		executeSave(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveTablePerClassPostgres(SaveTablePerClassPostgresState state)
	{
		executeSave(state);
	}
	
	@State(Scope.Thread)
	public static class SaveSingleTableMariaDbState extends AbstractSaveState
	{
		public SaveSingleTableMariaDbState()
		{
			super(mariaDbFactory, Book.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SaveSingleTablePostgresState extends AbstractSaveState
	{
		public SaveSingleTablePostgresState()
		{
			super(postgresFactory, Book.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveSingleTableMariaDb(SaveSingleTableMariaDbState state)
	{
		executeSave(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveSingleTablePostgres(SaveSingleTablePostgresState state)
	{
		executeSave(state);
	}
	
	@State(Scope.Thread)
	public static class SaveJoinedMariaDbState extends AbstractSaveState
	{
		public SaveJoinedMariaDbState()
		{
			super(mariaDbFactory, Pet.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SaveJoinedPostgresState extends AbstractSaveState
	{
		public SaveJoinedPostgresState()
		{
			super(postgresFactory, Pet.class);
		}
	}
	
	private void executeSave(AbstractSaveState state)
	{
		state.em.persist(state.object);
		state.em.flush();
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveJoinedMariaDb(SaveJoinedMariaDbState state)
	{
		executeSave(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveJoinedPostgres(SaveJoinedPostgresState state)
	{
		executeSave(state);
	}
	
	@State(Scope.Thread)
	public static class SaveMappedSuperclassMariaDbState extends AbstractSaveState
	{
		public SaveMappedSuperclassMariaDbState()
		{
			super(mariaDbFactory, Employee.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SaveMappedSuperclassPostgresState extends AbstractSaveState
	{
		public SaveMappedSuperclassPostgresState()
		{
			super(postgresFactory, Employee.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveMappedSuperclassMariaDb(SaveMappedSuperclassMariaDbState state)
	{
		executeSave(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSaveMappedSuperclassPostgres(SaveMappedSuperclassPostgresState state)
	{
		executeSave(state);
	}
	
	public static abstract class AbstractSelectState extends AbstractState
	{
		public EntityManager em;
		protected List<Long> entityIds;
		public Long id;
		
		public AbstractSelectState(EntityManagerFactory factory, Class<? extends Item> klass)
		{
			super(factory, klass);
		}
		
		@Setup(Level.Trial)
		public void setupTrial()
		{
			super.setupTrial();
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();
			entityIds = em.createQuery("SELECT o.id FROM " + klass.getCanonicalName() + " o").getResultList();
			em.getTransaction().commit();
			em.close();
		}
		
		@Setup(Level.Iteration)
		public void setupEachIter()
		{
			em = factory.createEntityManager();
		}
		
		@TearDown(Level.Iteration)
		public void tearDownEachIter()
		{
			em.close();
		}
		
		@Setup(Level.Invocation)
		public void setupEach()
		{
			this.id = entityIds.get(random.nextInt(entityIds.size()));
		}
	}
	
	@State(Scope.Thread)
	public static class SelectJoinedMariaDbState extends AbstractSelectState
	{
		public SelectJoinedMariaDbState()
		{
			super(mariaDbFactory, Pet.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SelectJoinedPostgresState extends AbstractSelectState
	{
		public SelectJoinedPostgresState()
		{
			super(postgresFactory, Pet.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectJoinedMariaDb(SelectJoinedMariaDbState state)
	{
		executeSelect(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectJoinedPostgres(SelectJoinedPostgresState state)
	{
		executeSelect(state);
	}
	
	@State(Scope.Thread)
	public static class SelectMappedSuperclassMariaDbState extends AbstractSelectState
	{
		public SelectMappedSuperclassMariaDbState()
		{
			super(mariaDbFactory, Employee.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SelectMappedSuperclassPostgresState extends AbstractSelectState
	{
		public SelectMappedSuperclassPostgresState()
		{
			super(postgresFactory, Employee.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectMappedSuperclassMariaDb(SelectMappedSuperclassMariaDbState state)
	{
		executeSelect(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectMappedSuperclassPostgres(SelectMappedSuperclassPostgresState state)
	{
		executeSelect(state);
	}
	
	@State(Scope.Thread)
	public static class SelectSingleTableMariaDbState extends AbstractSelectState
	{
		public SelectSingleTableMariaDbState()
		{
			super(mariaDbFactory, Book.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SelectSingleTablePostgresState extends AbstractSelectState
	{
		public SelectSingleTablePostgresState()
		{
			super(postgresFactory, Book.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectSingleTableMariaDb(SelectSingleTableMariaDbState state)
	{
		executeSelect(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectSingleTablePostgres(SelectSingleTablePostgresState state)
	{
		executeSelect(state);
	}
	
	@State(Scope.Thread)
	public static class SelectTablePerClassMariaDbState extends AbstractSelectState
	{
		public SelectTablePerClassMariaDbState()
		{
			super(mariaDbFactory, Car.class);
		}
	}
	
	@State(Scope.Thread)
	public static class SelectTablePerClassPostgresState extends AbstractSelectState
	{
		public SelectTablePerClassPostgresState()
		{
			super(postgresFactory, Car.class);
		}
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectTablePerClassMariaDb(SelectTablePerClassMariaDbState state)
	{
		executeSelect(state);
	}
	
	@Benchmark
	@Warmup(iterations = 15)
	public void executeSelectTablePerClassPostgres(SelectTablePerClassPostgresState state)
	{
		executeSelect(state);
	}
	
	private void executeSelect(AbstractSelectState state)
	{
		state.em.find(state.klass, state.id);
	}
}
