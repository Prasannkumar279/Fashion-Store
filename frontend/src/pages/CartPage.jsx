import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { apiFetch } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function CartPage() {
  const { token } = useAuth();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await apiFetch('/api/cart', { token });
      setItems(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    load();
  }, [load]);

  const updateQty = async (id, quantity) => {
    try {
      await apiFetch(`/api/cart/${id}`, {
        method: 'PATCH',
        token,
        body: JSON.stringify({ quantity }),
      });
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const remove = async (id) => {
    try {
      await apiFetch(`/api/cart/${id}`, { method: 'DELETE', token });
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const total = items.reduce((sum, i) => sum + Number(i.lineTotal), 0);

  if (loading) {
    return <p className="subtitle">Loading cart…</p>;
  }

  return (
    <>
      <h1 className="page-title">Your cart</h1>
      <p className="subtitle">
        <Link to="/">Continue shopping</Link>
      </p>
      {error && <p className="error-msg">{error}</p>}

      {items.length === 0 ? (
        <div className="empty-state">Your cart is empty.</div>
      ) : (
        <>
          <table className="cart-table">
            <thead>
              <tr>
                <th>Item</th>
                <th>Price</th>
                <th>Qty</th>
                <th>Line</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {items.map((row) => (
                <tr key={row.id}>
                  <td>
                    <div className="cart-row">
                      {row.imageUrl ? <img src={row.imageUrl} alt="" /> : null}
                      <span>{row.productName}</span>
                    </div>
                  </td>
                  <td>${Number(row.unitPrice).toFixed(2)}</td>
                  <td>
                    <div className="qty-control">
                      <button
                        type="button"
                        aria-label="Decrease"
                        onClick={() => updateQty(row.id, Math.max(1, row.quantity - 1))}
                      >
                        −
                      </button>
                      <span>{row.quantity}</span>
                      <button type="button" aria-label="Increase" onClick={() => updateQty(row.id, row.quantity + 1)}>
                        +
                      </button>
                    </div>
                  </td>
                  <td>${Number(row.lineTotal).toFixed(2)}</td>
                  <td>
                    <button type="button" className="btn btn-ghost" onClick={() => remove(row.id)}>
                      Remove
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <p style={{ marginTop: '1.25rem', fontSize: '1.1rem' }}>
            <strong>Subtotal:</strong> ${total.toFixed(2)}
          </p>
          <Link to="/payment" className="btn btn-primary" style={{ marginTop: '1rem', display: 'inline-flex' }}>
            Proceed to checkout
          </Link>
        </>
      )}
    </>
  );
}
