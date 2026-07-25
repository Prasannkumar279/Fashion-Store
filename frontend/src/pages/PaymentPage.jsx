import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { apiFetch } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function PaymentPage() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [cardholderName, setCardholderName] = useState('');
  const [cardLast4, setCardLast4] = useState('');
  const [billingAddress, setBillingAddress] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [order, setOrder] = useState(null);

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const data = await apiFetch('/api/checkout', {
        method: 'POST',
        token,
        body: JSON.stringify({
          cardholderName,
          cardLast4,
          billingAddress,
        }),
      });
      setOrder(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (order) {
    return (
      <>
        <div className="success-banner">
          <h2>Payment successful (demo)</h2>
          <p style={{ margin: 0, color: 'var(--muted)' }}>
            Order #{order.id} — total ${Number(order.totalAmount).toFixed(2)}. Stock has been updated and your cart
            cleared.
          </p>
        </div>
        <h3 style={{ fontFamily: 'var(--display)', fontWeight: 600 }}>Items</h3>
        <ul style={{ paddingLeft: '1.25rem', color: 'var(--muted)' }}>
          {order.lines.map((l) => (
            <li key={`${l.productId}-${l.productName}`}>
              {l.productName} × {l.quantity} — ${(Number(l.unitPrice) * l.quantity).toFixed(2)}
            </li>
          ))}
        </ul>
        <button type="button" className="btn btn-primary" style={{ marginTop: '1rem' }} onClick={() => navigate('/')}>
          Back to shop
        </button>
      </>
    );
  }

  return (
    <>
      <h1 className="page-title">Checkout</h1>
      <p className="subtitle">
        Demo payment only — no real charges. <Link to="/cart">Edit cart</Link>
      </p>
      <form className="form-stack" onSubmit={submit} style={{ maxWidth: 480 }}>
        <div className="field">
          <label htmlFor="nm">Cardholder name</label>
          <input
            id="nm"
            required
            value={cardholderName}
            onChange={(e) => setCardholderName(e.target.value)}
            autoComplete="cc-name"
          />
        </div>
        <div className="field">
          <label htmlFor="last4">Card last 4 digits</label>
          <input
            id="last4"
            required
            pattern="\d{4}"
            maxLength={4}
            placeholder="4242"
            value={cardLast4}
            onChange={(e) => setCardLast4(e.target.value.replace(/\D/g, '').slice(0, 4))}
            inputMode="numeric"
            autoComplete="cc-number"
          />
        </div>
        <div className="field">
          <label htmlFor="addr">Billing address</label>
          <textarea
            id="addr"
            required
            rows={3}
            value={billingAddress}
            onChange={(e) => setBillingAddress(e.target.value)}
          />
        </div>
        {error && <p className="error-msg">{error}</p>}
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Processing…' : 'Pay now'}
        </button>
      </form>
    </>
  );
}
