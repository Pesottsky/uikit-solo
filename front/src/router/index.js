import { createRouter, createWebHistory } from 'vue-router'

import navigationGuard from './navigationGuard';

import publicRouter from './public.router';
import companyRouter from './company.router';
import freelancerRouter from './freelancer.router';

const routes = [
	...publicRouter,
	...companyRouter,
	...freelancerRouter,
];

const router = createRouter({
	history: createWebHistory(),
	routes,
	strict: true
})

router.beforeEach(navigationGuard);

export default router
