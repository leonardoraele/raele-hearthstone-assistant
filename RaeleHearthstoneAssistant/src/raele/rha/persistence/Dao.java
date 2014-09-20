package raele.rha.persistence;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class Dao implements AutoCloseable {
	
	public static void boot(String persistenceUnit) {
		getFactory(persistenceUnit);
	}
	
	private static HashMap<String, EntityManagerFactory> factoryMap = new HashMap<>();
	private static synchronized EntityManagerFactory getFactory(String persistenceUnit)
	{
		EntityManagerFactory factory = factoryMap.get(persistenceUnit);
		if (factory == null)
		{
			factory = Persistence.createEntityManagerFactory(persistenceUnit);
			factoryMap.put(persistenceUnit, factory);
		}
		return factory;
	}
	
	public static synchronized void closeAll()
	{
		for (EntityManagerFactory factory : factoryMap.values())
		{
			try {
				factory.close();
			} catch (Exception e) {}
		}
	}
	
	private EntityManager manager;
	private String persistenceUnit;

	public Dao(String persistenceUnit)
	{
		EntityManagerFactory factory = getFactory(persistenceUnit);
		this.manager = factory.createEntityManager();
		this.persistenceUnit = persistenceUnit;
	}
	
	public EntityManager getEntityManager()
	{
		return this.manager;
	}
	
	public void beginTransaction()
	{
		EntityTransaction transaction = this.manager.getTransaction();
		if (!transaction.isActive())
		{
			transaction.begin();
		}
	}
	
	public void commitTransaction()
	{
		EntityTransaction transaction = this.manager.getTransaction();
		if (transaction.isActive())
		{
			transaction.commit();
		}
	}
	
	public void rollbackTransaction()
	{
		EntityTransaction transaction = this.manager.getTransaction();
		if (transaction.isActive())
		{
			transaction.rollback();
		}
	}
	
	public void insert(Object entity)
	{
		boolean active = this.manager.getTransaction().isActive();
		if (!active) this.manager.getTransaction().begin();
		this.manager.persist(entity);
		if (!active) this.manager.getTransaction().commit();
	}
	
	public <Entity extends Object> Entity selectById(Long id, Class<Entity> entityClass)
	{
		return this.manager.find(entityClass, id);
	}
	
	public <Entity extends Object> List<Entity> selectAll(Class<Entity> entityClass)
	{
		String jpql = "SELECT entity FROM " + entityClass.getName() + " entity";
		TypedQuery<Entity> query = this.manager.createQuery(jpql, entityClass);
		return query.getResultList();
	}
	
	public <Entity extends Object> void update(Entity entity, Class<Entity> entityClass)
	{
		boolean active = this.manager.getTransaction().isActive();
		if (!active) this.manager.getTransaction().begin();
		this.manager.merge(entity);
		if (!active) this.manager.getTransaction().commit();
	}

	public <Entity extends Object> void delete(Entity entity, Class<Entity> entityClass)
	{
		boolean active = this.manager.getTransaction().isActive();
		if (!active) this.manager.getTransaction().begin();
		Object id = this.manager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
		Object managedEntity = this.manager.find(entityClass, id);
		this.manager.remove(managedEntity);
		if (!active) this.manager.getTransaction().commit();
	}
	
	public void refresh()
	{
		try {
			this.manager.close();
		} catch (Exception e) {}
		
		this.manager = getFactory(persistenceUnit).createEntityManager();
	}

	@Override
	public void close() {
		if (this.manager.isOpen())
		{
			if (this.manager.getTransaction().isActive())
			{
				this.manager.getTransaction().rollback();
			}
			this.manager.close();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			this.close();
		} finally {
			super.finalize();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String manager = this.manager.isOpen() ? "opened" : "closed";
		String transaction = this.manager.getTransaction().isActive() ? "active" : "inactive";

		builder.append("Dao (manager ");
		builder.append(manager);
		builder.append(", transaction ");
		builder.append(transaction);
		builder.append(")");
		
		return builder.toString();
	}
	
}
