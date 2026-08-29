import sqlite3

DB_PATH = r"C:\Users\Joshua\IdeaProjects\InventoryManagementSystem\data\inventory.db"

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

# 1. Add the column only if it's missing
cur.execute("PRAGMA table_info(products)")
existing_cols = [row[1] for row in cur.fetchall()]
if "product_type" not in existing_cols:
    cur.execute("ALTER TABLE products ADD COLUMN product_type TEXT DEFAULT 'GENERAL'")
    print("Added product_type column.")
else:
    print("product_type column already exists.")

# 2. Backfill using the same category -> type mapping used in ProductService.deriveProductType()
cur.execute("SELECT id, name FROM categories")
category_names = {row[0]: (row[1] or "").strip().upper() for row in cur.fetchall()}

category_to_type = {
    "ELECTRONICS": "ELECTRONICS",
    "CLOTHING": "CLOTHING",
    "FOOD": "GROCERY",
    "BEVERAGES": "GROCERY",
    "TOFFEES": "GROCERY",
}

cur.execute("SELECT id, category_id FROM products")
products = cur.fetchall()

for product_id, category_id in products:
    category_name = category_names.get(category_id, "")
    product_type = category_to_type.get(category_name, "GENERAL")
    cur.execute("UPDATE products SET product_type = ? WHERE id = ?", (product_type, product_id))

conn.commit()

# 3. Show the result
print("\nFinal state:")
cur.execute("SELECT id, name, category_id, product_type FROM products")
for row in cur.fetchall():
    print(row)

conn.close()
print("\nDone.")
