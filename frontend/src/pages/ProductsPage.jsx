import { useCallback, useEffect, useState } from 'react';
import { apiFetch } from '../api/client';
import { useAuth } from '../context/AuthContext';

const CATEGORIES = [
  { value: '', label: 'All categories' },
  { value: 'shirts', label: 'Shirts' },
  { value: 'pants', label: 'Pants' },
  { value: 'shoes', label: 'Shoes' },
  { value: 'knitwear', label: 'Knitwear' },
  { value: 'accessories', label: 'Accessories' },
  { value: 'electronics', label: 'Electronics' }, 
];


export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [category, setCategory] = useState('');
  const [brand, setBrand] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [q, setQ] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    const params = new URLSearchParams();
    if (category) params.set('category', category);
    if (brand.trim()) params.set('brand', brand.trim());
    if (minPrice !== '') params.set('minPrice', minPrice);
    if (maxPrice !== '') params.set('maxPrice', maxPrice);
    if (q.trim()) params.set('q', q.trim());
    const qs = params.toString();
    try {
    const data = await apiFetch(`/api/products${qs ? `?${qs}` : ''}`);
    console.log("DATA FROM API ", data);  
    setProducts(data);
  } catch (e) {
    // try {
    //   const data = await apiFetch(`/api/products${qs ? `?${qs}` : ''}`);
    //   setProducts(data);
    // } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [category, brand, minPrice, maxPrice, q]);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <>
      <h1 className="page-title">New arrivals</h1>
      <p className="subtitle">Filter by category, brand, price, or search by name.</p>

      <div className="filters">
        <div className="field">
          <label htmlFor="cat">Category</label>
          <select id="cat" value={category} onChange={(e) => setCategory(e.target.value)}>
            {CATEGORIES.map((c) => (
              <option key={c.value || 'all'} value={c.value}>
                {c.label}
              </option>
            ))}
          </select>
        </div>
        <div className="field">
          <label htmlFor="brand">Brand</label>
          <input
            id="brand"
            placeholder="e.g. noir"
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
          />
        </div>
        <div className="field">
          <label htmlFor="min">Min price</label>
          <input
            id="min"
            type="number"
            min="0"
            step="1"
            placeholder="0"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
          />
        </div>
        <div className="field">
          <label htmlFor="max">Max price</label>
          <input
            id="max"
            type="number"
            min="0"
            step="1"
            placeholder="500"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
          />
        </div>
        <div className="field">
          <label htmlFor="q">Search</label>
          <input
            id="q"
            placeholder="Name or description"
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </div>
        <button type="button" className="btn btn-primary" onClick={load}>
          Apply filters
        </button>
      </div>

      {error && <p className="error-msg">{error}</p>}
      {loading && <p className="subtitle">Loading…</p>}

      {!loading && !error && products.length === 0 && (
        <div className="empty-state">No products match your filters.</div>
      )}

      <div className="card-grid">
        {products.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
    </>
  );
}

function ProductCard({ product }) {
  const { token, isAuthenticated } = useAuth();
  const [msg, setMsg] = useState(null);
  const [adding, setAdding] = useState(false);

  const add = async () => {
    if (!isAuthenticated) {
      setMsg('Log in to add items to your cart.');
      return;
    }
    setAdding(true);
    setMsg(null);
    try {
      await apiFetch('/api/cart', {
        method: 'POST',
        token,
        body: JSON.stringify({ productId: product.id, quantity: 1 }),
      });
      setMsg('Added to cart');
    } catch (e) {
      setMsg(e.message);
    } finally {
      setAdding(false);
    }
  };

  return (
    <article className="card">
      <img src={product.imageUrl} alt="" loading="lazy" />
      <div className="card-body">
        <h3>{product.name}</h3>
        <div className="card-meta">
          {product.category}
          {product.brand ? ` · ${product.brand}` : ''}
        </div>
        <p style={{ margin: 0, fontSize: '0.9rem', color: 'var(--muted)', flex: 1 }}>
          {product.description}
        </p>
        <div className="price">${Number(product.price).toFixed(2)}</div>
        <button type="button" className="btn btn-primary" style={{ marginTop: '0.75rem' }} onClick={add} disabled={adding}>
          {adding ? 'Adding…' : 'Add to cart'}
        </button>
        {msg && (
          <p className="error-msg" style={{ marginTop: '0.5rem', color: msg.includes('Added') ? 'var(--accent)' : undefined }}>
            {msg}
          </p>
        )}
      </div>
    </article>
  );
}
