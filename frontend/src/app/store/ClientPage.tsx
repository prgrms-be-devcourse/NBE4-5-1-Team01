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
    { id: number; name: string; price: number; quantity: number }[]
  >([]);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [email, setEmail] = useState("");
  const [address, setAddress] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [totalPrice, setTotalPrice] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const safeSearchResults = searchResults ?? [];
  const [isSearching, setIsSearching] = useState(false);
  const scrollRefs = useRef<(HTMLDivElement | null)[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<any>(null);

  const openModal = (item: any) => {
    setSelectedItem(item);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedItem(null);
  };

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

  const validateEmail = () => !(!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email));

  const addToCart = (itemId: number, itemName: string, itemPrice: number) => {
    setCart((prevCart) => {
      const existingItem = prevCart.find((item) => item.id === itemId);
      let newCart;

      if (existingItem) {
        newCart = prevCart.map((item) =>
          item.id === itemId ? { ...item, quantity: item.quantity + 1 } : item
        );
      } else {
        newCart = [
          ...prevCart,
          { id: itemId, name: itemName, price: itemPrice, quantity: 1 },
        ];
      }

      const newTotalPrice = newCart.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
      setTotalPrice(newTotalPrice);

      return newCart;
    });
  };

  const removeFromCart = (itemId: number) => {
    setCart((prevCart) => {
      const newCart = prevCart.filter((item) => item.id !== itemId);

      // 총 가격 업데이트
      const newTotalPrice = newCart.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
      setTotalPrice(newTotalPrice);

      return newCart;
    });
  };

  const updateCartQuantity = (itemId: number, newQuantity: number) => {
    setCart((prevCart) =>
      prevCart.map((item) =>
        item.id === itemId ? { ...item, quantity: newQuantity } : item
      )
    );

    // 총 가격 업데이트
    const newTotalPrice = cart.reduce(
      (sum, item) =>
        sum + item.price * (item.id === itemId ? newQuantity : item.quantity),
      0
    );
    setTotalPrice(newTotalPrice);
  };

  const getItemInventory = (itemId: number) => {
    // 현재 장바구니에 있는 상품의 카테고리를 찾음
    for (const row of rows) {
      const item = row.items.find((i) => i.id === itemId);
      if (item) return item.inventory;
    }
    return 1; // 기본적으로 최소 1개
  };

  const handleCheckout = async () => {
    if (!email || !address || !zipCode) {
      alert("이메일, 주소, 우편번호를 입력해주세요.");
      return;
    }

    if(!validateEmail()) {
      alert("이메일 형식이 올바르지 않습니다.");
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

    for (const item of cart) {
      const matchingItem = rows
        .flatMap(({ items }) => items)
        .find((product) => product.id === item.id);

      if (!matchingItem || matchingItem.inventory < item.quantity) {
        alert(
          `${item.name}"의 재고가 부족합니다. (남은 재고: ${
            matchingItem?.inventory ?? 0
          }개)`
        );
        return;
      }
    }

    const orderData = { email, address, zipCode, productQuantities };

    try {
      const response = await client.POST("/GCcoffee/orders", {
        body: orderData,
      });

      if (response?.data) {
        alert("주문이 성공적으로 완료되었습니다!");

        for (const item of cart) {
          const matchingItem = rows
            .flatMap(({ items }) => items)
            .find((product) => product.id === item.id);

          if (matchingItem) {
            matchingItem.inventory -= item.quantity;
          }
        }

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

          {/* 고객 안내 문구 추가 */}
          <p className="text-sm text-gray-600 mt-2">
            당일 오후 2시 이후의 주문은 다음날 배송을 시작합니다.
          </p>
        </div>

        {safeSearchResults.length > 0 ? (
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {safeSearchResults.map((item) => (
              <div key={item.id} className="border rounded-lg p-3 shadow-md">
                <div
                  className="block cursor-pointer"
                  onClick={() => openModal(item)}
                >
                  <img
                    src={item.imageUrl}
                    alt={item.name}
                    className="w-full h-40 object-cover rounded-lg"
                  />
                  <h3 className="text-lg font-semibold mt-2">{item.name}</h3>
                  <p className="text-sm text-gray-700 font-bold">
                    {item.price.toLocaleString()}원
                  </p>
                </div>
                <Button
                  className="mt-3 w-full"
                  onClick={() => addToCart(item.id, item.name, item.price)}
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
                {category === "HAND_DRIP" ? "Hand Drip" : category === "DECAF" ? "Decaf" : "Tea"}
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
                    <div
                      className="block cursor-pointer"
                      onClick={() => openModal(item)}
                    >
                      <img
                        src={item.imageUrl}
                        alt={item.name}
                        className="w-full h-40 object-cover rounded-lg"
                      />
                      <h3 className="text-lg font-semibold mt-2">
                        {item.name}
                      </h3>
                      <p className="text-sm text-gray-700 font-bold">
                        {item.price.toLocaleString()}원
                      </p>
                    </div>
                    <Button
                      className="mt-3 w-full"
                      onClick={() => addToCart(item.id, item.name, item.price)}
                    >
                      장바구니 추가
                    </Button>
                  </div>
                ))}
              </div>

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
          onClick={() => setIsCartOpen(true)}
        >
          <ShoppingCart size={24} />
        </button>

        {isCartOpen && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-end z-40">
            <div className="w-80 bg-white h-full shadow-lg p-5 flex flex-col">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-lg font-bold">장바구니</h2>
                <button
                  className="text-gray-500 hover:text-gray-800"
                  onClick={() => setIsCartOpen(false)}
                >
                  <X size={24} />
                </button>
              </div>

              {cart.length === 0 ? (
                <p className="text-gray-600">장바구니가 비어 있습니다.</p>
              ) : (
                <>
                  <ul className="flex-1 overflow-y-auto">
                    {cart.map((item) => (
                      <li
                        key={item.id}
                        className="border-b py-2 flex justify-between items-center"
                      >
                        <span className="text-sm">{item.name}</span>
                        <div className="flex items-center gap-2">
                          <Button
                            className="p-2 text-sm"
                            onClick={() =>
                              updateCartQuantity(item.id, item.quantity - 1)
                            }
                            disabled={item.quantity <= 1}
                          >
                            -
                          </Button>

                          <span className="w-6 text-center">
                            {item.quantity}
                          </span>
                          <Button
                            className="p-2 text-sm"
                            onClick={() =>
                              updateCartQuantity(item.id, item.quantity + 1)
                            }
                            disabled={
                              item.quantity >= getItemInventory(item.id)
                            }
                          >
                            +
                          </Button>
                          <Button
                            className="p-2 text-sm text-white"
                            onClick={() => removeFromCart(item.id)}
                          >
                            제거
                          </Button>
                        </div>
                      </li>
                    ))}
                  </ul>

                  <div className="text-lg font-semibold text-gray-800 mt-4">
                    총 가격: {totalPrice.toLocaleString()}원
                  </div>

                  <div className="mt-4 space-y-2">
                    <Input
                      type="email"
                      placeholder="이메일 입력"
                      className="w-full"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                    />
                    <Input
                      type="text"
                      placeholder="주소 입력"
                      className="w-full"
                      value={address}
                      onChange={(e) => setAddress(e.target.value)}
                    />
                    <Input
                      type="text"
                      placeholder="우편번호 입력"
                      className="w-full"
                      value={zipCode}
                      onChange={(e) => setZipCode(e.target.value)}
                    />
                  </div>
                  <Button className="mt-4 w-full" onClick={handleCheckout}>
                    결제하기
                  </Button>
                </>
              )}
            </div>
          </div>
        )}

        {isModalOpen && selectedItem && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded-lg shadow-lg w-96">
              <div className="flex justify-between items-center mb-4">
                <h2 className="text-xl font-bold">{selectedItem.name}</h2>
                <button
                  className="text-gray-500 hover:text-gray-800"
                  onClick={closeModal}
                >
                  <X size={24} />
                </button>
              </div>
              <img
                src={selectedItem.imageUrl}
                alt={selectedItem.name}
                className="w-full max-h-[60vh] object-cover object-cover rounded-lg"
              />

              {/* 상세 정보 */}
              <div className="mt-3 space-y-2">
                <p className="text-gray-600">{selectedItem.description}</p>
                <p className="text-lg font-semibold">
                  가격: {selectedItem.price.toLocaleString()}원
                </p>
                <p className="text-sm text-gray-700">
                  <span className="font-semibold">재고:</span>{" "}
                  {selectedItem.inventory}개 남음
                </p>
                <p className="text-sm text-gray-700">
                  <span className="font-semibold">카테고리:</span>{" "}
                    {selectedItem.category === "HAND_DRIP" ? "핸드 드립" : selectedItem.category === "DECAF" ? "디카페인" : "티"}

                </p>
              </div>

              <Button
                className="mt-4 w-full"
                onClick={() =>
                  addToCart(
                    selectedItem.id,
                    selectedItem.name,
                    selectedItem.price
                  )
                }
              >
                장바구니 추가
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
