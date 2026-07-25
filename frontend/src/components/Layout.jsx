import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <div className="layout">
      <header className="nav">
        <NavLink to="/" className="nav-brand">
          Fashion Store
        </NavLink>
        <nav className="nav-links">
          <NavLink to="/" end className={({ isActive }) => (isActive ? 'active' : undefined)}>
            Shop
          </NavLink>
          <NavLink to="/cart" className={({ isActive }) => (isActive ? 'active' : undefined)}>
            Cart
          </NavLink>
          {user ? (
            <>
              <NavLink to="/payment" className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Checkout
              </NavLink>
              <span style={{ color: 'var(--muted)', fontSize: '0.9rem' }}>{user.fullName}</span>
              <button
                type="button"
                className="btn btn-ghost"
                onClick={() => {
                  logout();
                  navigate('/');
                }}
              >
                Log out
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Log in
              </NavLink>
              <NavLink to="/register">
                <button type="button" className="btn btn-primary">
                  Register
                </button>
              </NavLink>
            </>
          )}
        </nav>
      </header>
      <Outlet />
    </div>
  );
}
