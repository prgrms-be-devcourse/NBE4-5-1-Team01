"use client";
import { useState, useEffect, useRef } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ShoppingCart, X, ChevronLeft, ChevronRight } from "lucide-react";
import client from "@/lib/backend/client";

export default function ClientPage({
  rows,
}: {
  rows: {
    category: string;
    items: {
      id: number;
      name: string;
      description: string;
      price: number;
      imageUrl: string;
      inventory: number;
      category: string;
    }[];
    totalPages: number;
  }[];
}) {
  const [cart, setCart] = useState<
    { id: number; name: string; quantity: number }[]
  >([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [zipCode, setZipCode] = useState("");

  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const safeSearchResults = searchResults ?? [];
  const [isSearching, setIsSearching] = useState(false);
  const scrollRefs = useRef<(HTMLDivElement | null)[]>([]);

  useEffect(() => {
    const storedCart = localStorage.getItem("cart");
    if (storedCart) {
      setCart(JSON.parse(storedCart));
    }
  }, []);

  useEffect(() => {
    localStorage.setItem("cart", JSON.stringify(cart));
  }, [cart]);

  useEffect(() => {
    if (searchTerm.trim() === "") {
      setSearchResults([]);
    }
  }, [searchTerm]);

  const addToCart = (itemId: number, itemName: string) => {
    setCart((prevCart) => {
      const existingItem = prevCart.find((item) => item.id === itemId);
      if (existingItem) {
        return prevCart.map((item) =>
          item.id === itemId ? { ...item, quantity: item.quantity + 1 } : item
        );
      } else {
        return [...prevCart, { id: itemId, name: itemName, quantity: 1 }];
      }
    });
  };

  const removeFromCart = (itemId: number) => {
    setCart((prevCart) => prevCart.filter((item) => item.id !== itemId));
  };

  const handleCheckout = async () => {
    if (!email || !address || !zipCode) {
      alert("이메일, 주소, 우편번호를 입력해주세요.");
      return;
    }

    if (cart.length === 0) {
      alert("장바구니가 비어 있습니다.");
      return;
    }

    const productQuantities = cart.reduce((acc, item) => {
      acc[item.id] = item.quantity;
      return acc;
    }, {} as Record<number, number>);

    const orderData = { email, address, zipCode, productQuantities };

    try {
      const response = await client.POST("/GCcoffee/orders", {
        body: orderData,
      });

      if (response?.data) {
        alert(`주문 성공`);
        setCart([]);
        localStorage.removeItem("cart");
        setEmail("");
        setAddress("");
        setZipCode("");
        setIsCartOpen(false);
      }
    } catch (error) {
      console.error("주문 요청 실패:", error);
      alert("주문에 실패했습니다. 다시 시도해주세요.");
    }
  };

  const toggleCart = () => setIsCartOpen((prev) => !prev);

  const handleSearch = async () => {
    if (!searchTerm.trim()) return;
    setIsSearching(true);
    try {
      const response = await client.GET("/GCcoffee/search", {
        params: {
          query: {
            keyword: searchTerm,
            page: 0,
            pageSize: 10,
            sort: "asc",
          },
        },
      });

      if (response?.data) {
        if (response.data.data && Array.isArray(response.data.data.items)) {
          setSearchResults(response.data.data.items);
        } else {
          alert("데이터를 불러오는 데 실패했습니다.");
        }
      }
    } catch (error) {
      alert("검색에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setIsSearching(false);
    }
  };

  return (
    <div className="relative min-h-screen bg-gray-100">
      <div className="w-full py-10 px-5">
        <div className="flex flex-col items-center mb-6">
          <div className="w-full max-w-md flex items-center gap-2">
            <Input
              type="text"
              placeholder="상품 검색..."
              className="flex-1 px-4 py-2 border rounded-lg shadow-sm"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <Button onClick={handleSearch} disabled={isSearching}>
              검색
            </Button>
          </div>
        </div>

        {safeSearchResults.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {safeSearchResults.map((item) => (
              <div key={item.id} className="border rounded-lg p-3 shadow-md">
                <div className="block cursor-pointer">
                  <img
                    src={item.imageUrl}
                    alt={item.name}
                    className="w-full h-40 object-cover rounded-lg"
                  />
                  <h3 className="text-lg font-semibold mt-2">{item.name}</h3>
                  <p className="text-sm text-gray-700 font-bold">
                    {item.price}원
                  </p>
                </div>
                <Button
                  className="mt-3 w-full"
                  onClick={() => addToCart(item.id, item.name)}
                >
                  장바구니 추가
                </Button>
              </div>
            ))}
          </div>
        ) : (
          rows.map(({ category, items }, index) => (
            <div key={category} className="mb-10 relative">
              <h2 className="text-2xl font-semibold mb-4 text-gray-800">
                {category}
              </h2>
              <button
                onClick={() =>
                  scrollRefs.current[index]?.scrollBy({
                    left: -300,
                    behavior: "smooth",
                  })
                }
                className={`absolute left-2 top-1/2 transform -translate-y-1/2 p-2 rounded-full 
    bg-white shadow-lg z-20 transition-opacity duration-300 ${
      isCartOpen
        ? "opacity-0 bg-opacity-0 backdrop-filter backdrop-blur-sm"
        : "opacity-100"
    }`}
                style={{ pointerEvents: "auto" }}
              >
                <ChevronLeft size={24} className="text-gray-600" />
              </button>

              <div
                ref={(el) => (scrollRefs.current[index] = el)}
                className="relative flex space-x-4 overflow-x-hidden items-center scroll-container"
              >
                {items.map((item) => (
                  <div
                    key={item.id}
                    className="border rounded-lg p-3 shadow-md min-w-[250px]"
                  >
                    <img
                      src={item.imageUrl}
                      alt={item.name}
                      className="w-full h-40 object-cover rounded-lg"
                    />
                    <h3 className="text-lg font-semibold mt-2">{item.name}</h3>
                    <p className="text-sm text-gray-700 font-bold">
                      {item.price}원
                    </p>
                    <Button
                      className="mt-3 w-full"
                      onClick={() => addToCart(item.id, item.name)}
                    >
                      장바구니 추가
                    </Button>
                  </div>
                ))}
              </div>

              {/* 오른쪽 화살표 */}
              <button
                onClick={() =>
                  scrollRefs.current[index]?.scrollBy({
                    left: 300,
                    behavior: "smooth",
                  })
                }
                className="absolute right-0 top-1/2 transform -translate-y-1/2 p-2 rounded-full"
              >
                <ChevronRight size={24} className="text-gray-600" />
              </button>
            </div>
          ))
        )}

        <button
          className="fixed bottom-10 right-10 bg-blue-500 text-white p-4 rounded-full shadow-lg hover:bg-blue-600 transition-transform transform hover:scale-110"
          onClick={toggleCart}
        >
          <ShoppingCart size={24} />
        </button>
        {isCartOpen && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-end">
            <div className="w-80 bg-white h-full shadow-lg p-5 flex flex-col">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-bold">장바구니</h2>
                <button
                  className="text-gray-500 hover:text-gray-800"
                  onClick={toggleCart}
                >
                  <X size={24} />
                </button>
              </div>

              {cart.length === 0 ? (
                <p className="text-gray-600">장바구니가 비어 있습니다.</p>
              ) : (
                <ul className="flex-1 overflow-y-auto">
                  {cart.map((item) => (
                    <li
                      key={item.id}
                      className="border-b py-2 flex justify-between items-center"
                    >
                      <span>
                        {item.name} (수량: {item.quantity})
                      </span>
                      <Button
                        className="text-white"
                        onClick={() => removeFromCart(item.id)}
                        size="sm"
                      >
                        제거
                      </Button>
                    </li>
                  ))}
                </ul>
              )}

              {cart.length > 0 && (
                <>
                  <Input
                    type="email"
                    placeholder="이메일 입력"
                    className="w-full mb-2"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                  <Input
                    type="text"
                    placeholder="주소 입력"
                    className="w-full mb-2"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                  <Input
                    type="text"
                    placeholder="우편번호 입력"
                    className="w-full mb-2"
                    value={zipCode}
                    onChange={(e) => setZipCode(e.target.value)}
                  />

                  <Button onClick={handleCheckout}>결제하기</Button>
                </>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
