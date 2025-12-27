import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Layout from "../layout/index.vue"; // 引入布局组件

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: "/", redirect: "/login" },
    { path: "/login", name: "login", component: Login },
    { path: "/register", name: "register", component: Register },

    // --- 核心业务路由 ---
    {
      path: "/dashboard",
      component: Layout, // 父路由使用 Layout
      redirect: "/dashboard", // 默认跳到子路由
      children: [
        {
          path: "", // 空路径表示默认子路由
          name: "Dashboard",
          component: () => import("../views/dashboard/index.vue"),
        },
      ],
    },
    // 社团相关路由
    {
      path: "/club",
      component: Layout,
      children: [
        {
          path: "list", // 访问路径是 /club/list
          name: "ClubList",
          component: () => import("../views/club/ClubList.vue"),
        },
        {
          path: "/my-club", // 注意路径要和 Layout 菜单里的 index="/my-club" 一致
          name: "MyClub",
          component: () => import("../views/club/MyClub.vue"),
        },
        {
          // :id 是动态参数
          path: "/club/manage/:id",
          name: "ClubManage",
          component: () => import("../views/club/ClubManage.vue"),
        },
      ],
    },
    // 管理员专用路由
    {
      path: "/admin",
      component: Layout,
      children: [
        {
          path: "club-audit",
          name: "AdminClubAudit",
          component: () => import("../views/admin/AdminClubAudit.vue"),
        },
        {
          path: "club-manage",
          name: "AdminClubManage",
          component: () => import("../views/admin/AdminClubManage.vue"),
        },
        
      ],
    },
    // --- 活动相关路由 ---
    {
      path: "/activity",
      component: Layout,
      children: [
        {
          path: "list", // 访问路径：/activity/list (活动中心)
          name: "ActivityList",
          component: () => import("../views/activity/ActivityList.vue"),
        },
        {
          path: "my", // 访问路径：/activity/my
          name: "MyActivity",
          component: () => import("../views/activity/MyActivity.vue"),
        },
      ],
    },
  ],
});

export default router;
